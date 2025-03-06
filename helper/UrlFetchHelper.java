package cu.redcuba.helper;

import com.google.common.net.UrlEscapers;
import cu.redcuba.entity.Download;
import cu.redcuba.entity.Visit;
import cu.redcuba.exception.CustomRedirectMaxLimitException;
import cu.redcuba.repository.DownloadRepository;
import cu.redcuba.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Funciones de ayuda para visitar y descargar URLs manteniendo una caché diaria.
 */
public class UrlFetchHelper {

    private static final Logger LOG = Logger.getLogger(UrlFetchHelper.class.getName());

    @Value("${helper.urlFetch.userAgent}")
    private String userAgent;

    @Value("${helper.urlFetch.connectTimeout}")
    private int connectTimeout;

    @Value("${helper.urlFetch.readTimeout}")
    private int readTimeout;

    @Value("${helper.urlFetch.customRedirectMaxLimit}")
    private int customRedirectMaxLimit;

    @Autowired
    private DownloadRepository downloadRepository;

    @Autowired
    private VisitRepository visitRepository;

    private boolean acceptCompression = false;

    private final Pattern refreshPattern = Pattern.compile("\\d+\\s*;\\s*url=(.+)");

    /**
     * Establecer que se aceptan métodos de compresión en la respuesta.
     *
     * @param acceptCompression Se acepta o no compresión.
     */
    public void setAcceptCompression(boolean acceptCompression) {
        this.acceptCompression = acceptCompression;
    }

    /**
     * Obtener hash md5 de un texto.
     *
     * @param text Texto en cuestión.
     * @return Hash md5.
     */
    private String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            byte[] array = md.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            // Nothing to do here.
        }
        return null;
    }

    /**
     * Obtener una conexión a una URL, dispara una función recursiva.
     *
     * @param url URL en cuestión.
     * @return Conexión a la URL.
     * @throws IOException                     Error de entrada/salida.
     * @throws CustomRedirectMaxLimitException Error lanzado para evitar un abrazo fatal.
     */
    private URLConnection getUrlConnection(URL url) throws IOException, CustomRedirectMaxLimitException {
        int redirectCount = 0;
        return getUrlConnection(url, redirectCount);
    }

    /**
     * Obtener una conexión a una URL, función recursiva.
     *
     * @param url           URL en cuestión.
     * @param redirectCount Cantidad de redirecciones realizadas.
     * @return Conexión a la URL.
     * @throws IOException                     Error de entrada/salida.
     * @throws CustomRedirectMaxLimitException Error lanzado para evitar un abrazo fatal.
     */
    private URLConnection getUrlConnection(URL url, int redirectCount) throws IOException, CustomRedirectMaxLimitException {
        if (redirectCount > customRedirectMaxLimit) {
            throw new CustomRedirectMaxLimitException(String.format("Custom redirect max limit of %d reached", customRedirectMaxLimit));
        }
        HttpURLConnection.setFollowRedirects(true);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("User-Agent", userAgent);
        if (acceptCompression) {
            http.setRequestProperty("Accept-Encoding", "gzip,deflate");
        }
        http.setConnectTimeout(connectTimeout);
        http.setReadTimeout(readTimeout);
        http.setInstanceFollowRedirects(true);
        http.connect();
        // Procesar redirecciones específicas.
        switch (http.getResponseCode()) {
            case HttpURLConnection.HTTP_OK:
                // Seguir los refresh.
                // Redirección mediante un refrescamiento de la página:
                // http://www.otsukare.info/2015/03/26/refresh-http-header
                String refresh = http.getHeaderField("Refresh");
                if (null != refresh && !"".equals(refresh.trim())) {
                    Matcher matcher = refreshPattern.matcher(refresh);
                    if (matcher.matches()) {
                        http.disconnect();
                        // Obtener la dirección.
                        URL next = new URL(matcher.group(1).trim());
                        return getUrlConnection(next, ++redirectCount);
                    }
                }
                break;
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
            case HttpURLConnection.HTTP_SEE_OTHER:
                // Seguir los location.
                // El setFollowRedirect y el setInstanceFollowRedirects sólo
                // funcionan automáticamente cuando el protocolo redirigido es
                // el mismo. Ejemplo: http a http:
                // https://stackoverflow.com/questions/1884230/urlconnection-doesnt-follow-redirect
                // http://www.mkyong.com/java/java-httpurlconnection-follow-redirect-example/
                String location = http.getHeaderField("Location");
                if (null != location && !"".equals(location.trim())) {
                    http.disconnect();
                    // Lidiar con URLs relativas.
                    URL next = new URL(url, location);
                    return getUrlConnection(next, ++redirectCount);
                }
                break;
        }
        return http;
    }

    /**
     * Obtener el contenido de un flujo.
     *
     * @param input           Flujo de entrada.
     * @param contentEncoding Codificación del contenido.
     * @return Contenido en forma de texto.
     * @throws IOException Error de entrada/salida.
     */
    private String getContent(InputStream input, String contentEncoding) throws IOException {
        // Handle compressed responses
        Reader reader;
        if (contentEncoding != null && contentEncoding.contains("gzip")) {
            reader = new InputStreamReader(new GZIPInputStream(input));
        } else if (contentEncoding != null && contentEncoding.contains("deflate")) {
            reader = new InputStreamReader(new InflaterInputStream(input, new Inflater(true)));
        } else {
            reader = new InputStreamReader(input);
        }
        BufferedReader br = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }

    /**
     * Visitar una URL, la visita solo almacenará las cebeceras, no el contenido.
     *
     * @param address Dirección a visitar.
     * @param reVisit Visitar la dirección aunque ya haya sido visitada el mismo día.
     * @return Datos obtenidos en la visita.
     */
    public Visit visit(String address, boolean reVisit) {
        Visit visit = null;
        String urlHash = md5(address);
        Date visitDate = new Date();
        String host = "";
        short code = 0;
        String contentEncoding = "";
        String contentType = "";
        String cacheControl = "";
        String xFrameOptions = "";
        String xContentTypeOptions = "";
        String xXssProtection = "";

        String lastUrl = "";
        boolean visitAnyway = false;
        long startTime = 0, elapsedTime;
        try {
            visit = visitRepository.findByVisitedDateAndUrlHash(visitDate, urlHash);
            // Determinar cuando se visita la página.
            if (reVisit || visit == null) {
                visitAnyway = true;
            }
            // Visitar la página si se dan las condiciones.
            if (visitAnyway) {
                startTime = System.currentTimeMillis();
                URL url = new URL(address);
                host = url.getHost();
                SSLHelper.turnOffCertificateValidation();
                HttpURLConnection http = (HttpURLConnection) getUrlConnection(url);
                code = (short) http.getResponseCode();
                contentEncoding = http.getHeaderField("Content-Encoding");
                contentType = http.getHeaderField("Content-Type");
                cacheControl = http.getHeaderField("Cache-Control");
                xFrameOptions = http.getHeaderField("X-Frame-Options");
                xContentTypeOptions = http.getHeaderField("X-Content-Type-Options");
                xXssProtection = http.getHeaderField("X-XSS-Protection");
                lastUrl = http.getURL().toString();
                http.disconnect();
            }
        } catch (UnknownHostException ex) {
            code = 51;
            LOG.log(Level.WARNING, "{0} {1}", new Object[]{ex.getClass(), ex.getMessage()});
        } catch (NoRouteToHostException ex) {
            code = 52;
            LOG.log(Level.WARNING, "{0} {1}", new Object[]{ex.getClass(), ex.getMessage()});
        } catch (SocketTimeoutException ex) {
            code = 53;
            LOG.log(Level.WARNING, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } catch (ConnectException ex) {
            code = 54;
            LOG.log(Level.WARNING, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } catch (ProtocolException ex) {
            code = 55;
            LOG.log(Level.WARNING, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } catch (CustomRedirectMaxLimitException ex) {
            code = 56;
            LOG.log(Level.WARNING, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } finally {
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        // Si fue visitada la página.
        if (visitAnyway) {
            // Si no existía la visita crearla.
            if (visit == null) {
                visit = new Visit(0L, visitDate, urlHash, address, host, code);
            }
            visit.setCode(code);
            visit.setElapsedTime(elapsedTime);
            visit.setContentEncoding(contentEncoding);
            visit.setContentType(contentType);
            visit.setCacheControl(cacheControl);
            visit.setXFrameOptions(xFrameOptions);
            visit.setXContentTypeOptions(xContentTypeOptions);
            visit.setXXssProtection(xXssProtection);
            visit.setModified(visitDate);
            visit.setLastUrl(lastUrl);
            // Guardar la entidad visitada.
            visitRepository.save(visit);
        }
        return visit;
    }

    /**
     * Visitar una URL, sin persistir en la base de datos los datos obtenidos.
     *
     * @param address Dirección a visitar.
     * @return Datos obtenidos en la visita.
     */
    public Visit visitUnstored(String address) {
        Visit visit;
        String urlHash = md5(address);
        Date visitDate = new Date();
        String host = "";
        short code = 0;
        String contentEncoding = "";
        String contentType = "";
        String cacheControl = "";
        String xFrameOptions = "";
        String xContentTypeOptions = "";
        String xXssProtection = "";
        String lastUrl = "";
        long startTime = 0, elapsedTime;
        try {
            // Visitar la página.
            startTime = System.currentTimeMillis();
            URL url = new URL(address);
            host = url.getHost();
            SSLHelper.turnOffCertificateValidation();
            HttpURLConnection http = (HttpURLConnection) getUrlConnection(url);
            code = (short) http.getResponseCode();
            contentEncoding = http.getHeaderField("Content-Encoding");
            contentType = http.getHeaderField("Content-Type");
            cacheControl = http.getHeaderField("Cache-Control");
            xFrameOptions = http.getHeaderField("X-Frame-Options");
            xContentTypeOptions = http.getHeaderField("X-Content-Type-Options");
            xXssProtection = http.getHeaderField("X-XSS-Protection");
            lastUrl = http.getURL().toString();
            http.disconnect();
        } catch (UnknownHostException ex) {
            code = 51;
            LOG.log(Level.WARNING, "{0} {1}", new Object[]{ex.getClass(), ex.getMessage()});
        } catch (NoRouteToHostException ex) {
            code = 52;
            LOG.log(Level.WARNING, "{0} {1}", new Object[]{ex.getClass(), ex.getMessage()});
        } catch (SocketTimeoutException ex) {
            code = 53;
            LOG.log(Level.WARNING, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } catch (ConnectException ex) {
            code = 54;
            LOG.log(Level.WARNING, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } catch (ProtocolException ex) {
            code = 55;
            LOG.log(Level.WARNING, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } catch (CustomRedirectMaxLimitException ex) {
            code = 56;
            LOG.log(Level.WARNING, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } finally {
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        // Crear la visita.
        visit = new Visit(0L, visitDate, urlHash, address, host, code);
        visit.setCode(code);
        visit.setElapsedTime(elapsedTime);
        visit.setContentEncoding(contentEncoding);
        visit.setContentType(contentType);
        visit.setCacheControl(cacheControl);
        visit.setXFrameOptions(xFrameOptions);
        visit.setXContentTypeOptions(xContentTypeOptions);
        visit.setXXssProtection(xXssProtection);
        visit.setModified(visitDate);
        visit.setLastUrl(lastUrl);
        return visit;
    }

    /**
     * Descargar contenido de una URL, se almacenarán las cebeceras y el contenido.
     *
     * @param address    Dirección a visitar.
     * @param reDownload Descargar el contenido aunque ya haya sido descargado el mismo día.
     * @return Datos obtenidos en la descarga.
     */
    public Download download(String address, boolean reDownload) {
        Download download = null;
        String urlHash = md5(address);
        Date downloadDate = new Date();
        String content = "";
        String host = "";
        short code = 0;
        String contentEncoding = "";
        String contentType = "";
        long contentLength = 0;
        String cacheControl = "";
        String xFrameOptions = "";
        String xContentTypeOptions = "";
        String xXssProtection = "";
        String lastUrl = "";
        boolean downloadAnyway = false;
        long startTime = 0, elapsedTime;
        try {
            download = downloadRepository.findByDownloadedDateAndUrlHash(downloadDate, urlHash);
            // Determinar cuando se descargará la página.
            if (reDownload || download == null || download.getContent() == null) {
                downloadAnyway = true;
            }
            // Descargar la página si se dan las condiciones.
            if (downloadAnyway) {
                startTime = System.currentTimeMillis();
                URL url = new URL(address);
                host = url.getHost();
                SSLHelper.turnOffCertificateValidation();
                HttpURLConnection http = (HttpURLConnection) getUrlConnection(url);
                code = (short) http.getResponseCode();
                contentEncoding = http.getHeaderField("Content-Encoding");
                contentType = http.getHeaderField("Content-Type");
                cacheControl = http.getHeaderField("Cache-Control");
                xFrameOptions = http.getHeaderField("X-Frame-Options");
                xContentTypeOptions = http.getHeaderField("X-Content-Type-Options");
                xXssProtection = http.getHeaderField("X-XSS-Protection");
                lastUrl = http.getURL().toString();
                // Obtener el contenido de la página.
                if (code == HttpURLConnection.HTTP_OK) {
                    content = getContent(http.getInputStream(), contentEncoding);
                    contentLength = content.getBytes().length;
                }
                http.disconnect();
            }
        } catch (UnknownHostException ex) {
            code = 51;
            LOG.log(Level.WARNING, "{0} {1}", new Object[]{ex.getClass(), ex.getMessage()});
        } catch (NoRouteToHostException ex) {
            code = 52;
            LOG.log(Level.WARNING, "{0} {1}", new Object[]{ex.getClass(), ex.getMessage()});
        } catch (SocketTimeoutException ex) {
            code = 53;
            LOG.log(Level.WARNING, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } catch (ConnectException ex) {
            code = 54;
            LOG.log(Level.WARNING, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } catch (ProtocolException ex) {
            code = 55;
            LOG.log(Level.WARNING, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } catch (CustomRedirectMaxLimitException ex) {
            code = 56;
            LOG.log(Level.WARNING, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "{0} {1} {2}", new Object[]{ex.getClass(), address, ex.getMessage()});
        } finally {
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        // Si fue descargada la página.
        if (downloadAnyway) {
            // Si no existía la descarga crearla.
            if (download == null) {
                download = new Download(0L, downloadDate, urlHash, address, host, code);
            }
            download.setCode(code);
            download.setElapsedTime(elapsedTime);
            download.setContentEncoding(contentEncoding);
            download.setContentType(contentType);
            download.setContentLength(contentLength);
            download.setCacheControl(cacheControl);
            download.setXFrameOptions(xFrameOptions);
            download.setXContentTypeOptions(xContentTypeOptions);
            download.setxXssProtection(xXssProtection);
            download.setContent(content);
            download.setModified(downloadDate);
            download.setLastUrl(lastUrl);
            // Guardar la entidad descargada.
            downloadRepository.save(download);
        }
        return download;
    }

    /**
     * Obtener URL codificada.
     *
     * @param resource URL sin codificar.
     * @return URL codificada.
     */
    public static String getUrlEncoded(String resource) {
        String[] urlParts = resource.split("\\?");
        // Codificar los segmentos dentro del camino.
        if (urlParts[0].contains("/")) {
            int firstIndex = urlParts[0].indexOf("/");
            String partBegin = urlParts[0].substring(0, firstIndex);
            String partEnd = urlParts[0].substring(firstIndex);
            String[] segments = partEnd.split("/");
            List<String> segmentsEncoded = new ArrayList<>();
            for (String segment : segments) {
                // Anteriormente se probó la siguiente alternativa, que no dió resultado: segment = URLDecoder.decode(segment, "UTF-8");
                segment = UrlEscapers.urlPathSegmentEscaper().escape(segment);
                segmentsEncoded.add(segment);
            }
            partEnd = String.join("/", segmentsEncoded);
            urlParts[0] = partBegin + partEnd;
        }
        String urlEncoded = urlParts[0];
        // Codificar los parámetros.
        if (urlParts.length > 1) {
            List<String> params = new ArrayList<>();
            String query = urlParts[1];
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                String value = pair.length > 1 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : "";
                params.add(URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8));
            }
            urlEncoded = urlEncoded + "?" + String.join("&", params);
        }
        return urlEncoded;
    }

    /**
     * Obtener una URL absoluta.
     *
     * @param resource URL relativa.
     * @param url      URL base a utilizar.
     * @return URL absoluta.
     */
    public static String getAbsolute(String resource, String url) {
        // Retornar el recurso si la url entrada está mal formada.
        String absoluteUrl = resource;
        try {
            URI uri = new URI(url);
            // Fix para evitar un hostname incorrecto cuando la URL origen no tiene path y el recurso no inicia con slash.
            if ("".equals(uri.getPath())) {
                url += "/";
                uri = new URI(url);
            }
            URI uriAbsolute = uri.resolve(resource);
            absoluteUrl = uriAbsolute.toString();
        } catch (URISyntaxException ex) {
            LOG.log(Level.SEVERE, "class: {0}, url: {1}, resource: {2}, message: {3}", new Object[]{ex.getClass(), url, resource, ex.getMessage()});
        }
        return absoluteUrl;
    }

}
