import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(80);
        staticFiles.location("/public");
        post("/urlinput", RequestHandler::inputHandler);
        get("/download/:id/:name", RequestHandler::downloadHandler);
        System.out.println("server is set.");
        init();
    }
}
