public class Compose {

    private final String name;
    private final String version;
    private  final String hash;
    private final String repo;
    private  final String path;

    public Compose(String name , String version,String hash,String repo,String path) {
        this.name = name;
        this.version = version;
        this.hash = hash;
        this.repo = repo;
        this.path = path;
    }

    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getHash() { return hash; }

    public String getRepo() {
        return repo;
    }

    public String getPath() {
        return path;
    }
    /*public String getId() {
        return id.nextString();
    }*/

    public  void ToString(){
        System.out.println(this.name +" FROM " + this.version + "  et sa version est  " + this.hash + "  recupere du repo " + this.repo + "  et le path " + this.path);
    }
}
