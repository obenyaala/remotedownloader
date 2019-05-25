import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TUID {
    private static List<TUID> tUIDList = new ArrayList<>();

    private String tUID ;
    private URL url;
    private static final Object LIST_LOCK = new Object();

    private TUID(String tUID, URL url) {
        this.tUID = tUID;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TUID)) return false;
        TUID tuid = (TUID) o;
        return tUID.equals(tuid.tUID) &&
                url.equals(tuid.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tUID, url);
    }

    public static TUID creatTUID(URL url){
        String uuid = UUID.randomUUID().toString();
        TUID tuid = new TUID(uuid,url);
        if (tUIDList.contains(tuid)){
            creatTUID(url);
        }
        synchronized (LIST_LOCK){
            tUIDList.add(tuid);
        }
        return tuid;
    }

    public String gettUID() {
        return tUID;
    }

    public URL getUrl() {
        return url;
    }

    public static void removeTUID(TUID tuid){
        synchronized (LIST_LOCK){
            tUIDList.remove(tuid);
        }
    }

    public static TUID getTUID(String tUID){
        synchronized (LIST_LOCK){
            if (tUIDList.size() > 0){
                for (TUID tuid : tUIDList) {
                    if (tuid.gettUID().equals(tUID)){
                        return tuid;
                    }
                }
            }
            return null;
        }
    }
}
