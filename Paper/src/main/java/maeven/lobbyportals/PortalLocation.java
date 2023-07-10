package maeven.lobbyportals;

public class PortalLocation {
    private String target;
    private double xpos;
    private double ypos;
    private double zpos;

    public PortalLocation(String pTarget, double px,double py,double pz)
    {
        this.target = pTarget;
        this.xpos = px;
        this.ypos = py;
        this.zpos = pz;
    }
    public String getTarget() {
        return target;
    }
    public double getXPos() {
        return xpos;
    }
    public double getYPos() {
        return ypos;
    }
    public double getZPos() {
        return zpos;
    }
}
