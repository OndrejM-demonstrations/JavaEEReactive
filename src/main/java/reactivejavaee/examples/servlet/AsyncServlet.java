package reactivejavaee.examples.servlet;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import reactivejavaee.cdi.boundary.Managed;

@WebServlet(urlPatterns = "/asyncServlet", asyncSupported = true)
public class AsyncServlet extends HttpServlet {

    @Inject
    @Managed
    private ExecutorService executorService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        AsyncContext asyncContext = req.startAsync();

        // get the NIO channel (something like a file descriptor)
        AsynchronousFileChannel fileChannel = 
                AsynchronousFileChannel
                        .open(htmlFilePathFrom(req), readOption(),
                                executorService);
        // read file, write to async servlet output, 
        // then finish the response
        new AsyncFileReader(fileChannel, StandardCharsets.UTF_8)
                .read()
                .thenComposeAsync(contents -> {
                    return new AsyncServletWriter(asyncContext)
                            .write(contents, StandardCharsets.UTF_8);
                }, executorService).thenRunAsync(() -> {
                    asyncContext.complete();
                }, executorService);
    }

    private static Set<StandardOpenOption> readOption() {
        return Collections.singleton(StandardOpenOption.READ);
    }

    private Path htmlFilePathFrom(HttpServletRequest req) {
        return Paths.get(req.getServletContext().getRealPath("/asyncServletResponse.html"));
    }

    public static class AsyncFileReader implements CompletionHandler<Integer, Object> {

        private CompletableFuture<String> fileReadfuture;
        private AsynchronousFileChannel fileChannel;
        private ByteBuffer buffer = ByteBuffer.allocate(1024);
        private long lastPosition;
        private StringBuilder fileContents;
        private Charset charset;

        public AsyncFileReader(AsynchronousFileChannel fileChannel, Charset charset) {
            this.fileChannel = fileChannel;
            this.lastPosition = 0;
            this.fileReadfuture = new CompletableFuture<>();
            this.fileContents = new StringBuilder();
            this.charset = charset;
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
                CharBuffer charBuffer = charset.decode(buffer);
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

    public static class AsyncServletWriter implements WriteListener {

        private final AsyncContext asyncContext;
        private ServletOutputStream outputStream;
        private byte[] data;
        private int bytesWritten;
        private boolean allWritten = false;
        private CompletableFuture<Void> fileWriteFuture;

        public AsyncServletWriter(AsyncContext asyncContext) {
            this.asyncContext = asyncContext;
        }

        @Override
        public void onWritePossible() throws IOException {
            while (!allWritten && outputStream.isReady()) {
                int bytesToWrite = Math.min(1024, data.length - bytesWritten);
                if (bytesToWrite > 0) {
                    outputStream.write(data, bytesWritten, bytesToWrite);
                    bytesWritten += 1024;
                } else {
                    allWritten = true;
                    fileWriteFuture.complete(null);
                }
            }
        }

        @Override
        public void onError(Throwable t) {
            fileWriteFuture.completeExceptionally(t);
        }

        public CompletableFuture<Void> write(String contents, Charset charset) {
            fileWriteFuture = new CompletableFuture();
            try {
                outputStream = asyncContext.getResponse().getOutputStream();
                data = contents.getBytes(charset);
                bytesWritten = 0;
                allWritten = false;
                outputStream.setWriteListener(this);
            } catch (IOException ex) {

                fileWriteFuture.completeExceptionally(ex);
            }
            return fileWriteFuture;
        }
    }
}
