package eu.ondrom.javaeereactive.examples.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/asyncServlet", asyncSupported = true)
public class AsyncServlet extends HttpServlet {

    @Resource
    private ManagedExecutorService executorService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        AsyncContext asyncContext = req.startAsync();
        
        Path pathToHtmlFile = getHtmlFilePath(req);
        Set<StandardOpenOption> readOption = Collections.singleton(StandardOpenOption.READ);
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(pathToHtmlFile, readOption, executorService);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        long position = 0;
        new AsyncFileReader(fileChannel, buffer, position)
            .read()
            .thenAccept(contents -> {
                try {
                    // TODO write asynchronusly using resp.getOutputStream().setWriteListener
                    asyncContext.getResponse().getOutputStream().print(contents);
                    asyncContext.complete();
                } catch (IOException ex) {
                    Logger.getLogger(AsyncServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
    }

    private Path getHtmlFilePath(HttpServletRequest req) {
        return Paths.get(req.getServletContext().getRealPath("/asyncServletResponse.html"));
    }

    private static class AsyncFileReader implements CompletionHandler<Integer, Object> {

        private CompletableFuture<String> fileReadfuture;
        private AsynchronousFileChannel fileChannel;
        private ByteBuffer buffer;
        private long lastPosition;
        private StringBuilder fileContents;

        public AsyncFileReader(AsynchronousFileChannel fileChannel, ByteBuffer buffer, long position) {
            this.fileChannel = fileChannel;
            this.buffer = buffer;
            this.lastPosition = position;
            this.fileReadfuture = new CompletableFuture<>();
            this.fileContents = new StringBuilder();
        }

        @Override
        public void completed(Integer result, Object attachment) {
            if (result == -1) {
                try {
                    fileChannel.close();
                    fileReadfuture.complete(fileContents.toString());
                } catch (IOException ex) {
                    fileReadfuture.completeExceptionally(ex);
                }
            } else {
                lastPosition += result;
                buffer.flip();
                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
                buffer.clear();
                fileContents.append(charBuffer);
                fileChannel.read(buffer, lastPosition, attachment, this);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            try {
                fileChannel.close();
            } catch (IOException ex) {
            }
            fileReadfuture.completeExceptionally(exc);
        }

        public CompletionStage<String> read() {
            completed(0, null);
            return fileReadfuture;
        }
    }
}    