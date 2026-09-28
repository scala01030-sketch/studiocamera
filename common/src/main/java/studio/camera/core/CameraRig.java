package studio.camera.core;

import java.util.ArrayDeque;
import java.util.Deque;


public final class CameraRig {
    public double x, y, z, yaw, pitch, roll, fov = 70, speed = 5;
    private final Deque<Snapshot> history = new ArrayDeque<>();
    private Snapshot gesture;
    public record Snapshot(double x, double y, double z, double yaw, double pitch,
                           double roll, double fov, double speed) {}
    public Snapshot snapshot() { return new Snapshot(x,y,z,yaw,pitch,roll,fov,speed); }
    public void restore(Snapshot s) { x=s.x; y=s.y; z=s.z; yaw=s.yaw; pitch=s.pitch; roll=s.roll; fov=s.fov; speed=s.speed; }
    public void begin(double x, double y, double z, double yaw, double pitch, double fov) {
        restore(new Snapshot(x,y,z,yaw,pitch,0,clamp(fov,10,150),5)); clearHistory();
    }
    public void beginGesture() { if(gesture==null) gesture=snapshot(); }
    public void endGesture() {
        if(gesture!=null && !gesture.equals(snapshot())) {
            if(history.size()==10) history.removeFirst(); history.addLast(gesture);
        }
        gesture=null;
    }
    public void clearHistory() { history.clear(); gesture=null; }
    public int undoCount() { return history.size(); }
    public boolean undo() { endGesture(); if(history.isEmpty()) return false; restore(history.removeLast()); return true; }
    public void look(double dx, double dy) { yaw=angle(yaw+dx*.15); pitch=clamp(pitch+dy*.15,-90,90); }
    public void zoom(double steps) { fov=clamp(fov-steps*2,10,150); }
    public void pan(double dx, double dy) {
        double a=Math.toRadians(yaw), p=Math.toRadians(pitch), r=Math.toRadians(roll);
        double rx=Math.cos(a), rz=Math.sin(a);
        double ux=-Math.sin(a)*Math.sin(p), uy=Math.cos(p), uz=Math.cos(a)*Math.sin(p);
        double scale=speed*.004*Math.tan(Math.toRadians(fov/2))/Math.tan(Math.toRadians(35));
        double cr=Math.cos(r), sr=Math.sin(r);
        x+=(-dx*(rx*cr+ux*sr)+dy*(ux*cr-rx*sr))*scale;
        y+=(-dx*uy*sr+dy*uy*cr)*scale;
        z+=(-dx*(rz*cr+uz*sr)+dy*(uz*cr-rz*sr))*scale;
    }
    public void move(double forward, double side, double vertical, double rollInput,
                     double zoomInput, boolean fast, double seconds) {
        double dt=clamp(seconds,0,.1), len=Math.sqrt(forward*forward+side*side+vertical*vertical);
        if(len>0) {
            double a=Math.toRadians(yaw), step=speed*dt*(fast?4:1)/len;
            x+=(-Math.sin(a)*forward+Math.cos(a)*side)*step;
            z+=(Math.cos(a)*forward+Math.sin(a)*side)*step; y+=vertical*step;
        }
        roll=angle(roll+rollInput*dt*45); fov=clamp(fov+zoomInput*dt*40,10,150);
    }
    public boolean apply(double[] v) {
        if(v.length!=8) return false;
        for(double n:v) if(!Double.isFinite(n)) return false;
        if(Math.abs(v[0])>29999984 || Math.abs(v[2])>29999984 || Math.abs(v[1])>20000000
                || v[4]<-90 || v[4]>90 || v[6]<10 || v[6]>150 || v[7]<.05 || v[7]>100) return false;
        beginGesture(); restore(new Snapshot(v[0],v[1],v[2],angle(v[3]),v[4],angle(v[5]),v[6],v[7])); endGesture(); return true;
    }
    public void preset(double cx, double cy, double cz, double facing, int side, double height, double width, double aspect) {
        beginGesture(); yaw=angle(facing+switch(side){case 0->180;case 1->0;case 2->90;default->-90;}); pitch=roll=0;
        double radius=Math.max(height,width)*.65;
        double distance=Math.max(2.5,radius/Math.tan(Math.toRadians(fov/2))/Math.max(.2,aspect));
        double a=Math.toRadians(yaw); x=cx+Math.sin(a)*distance; y=cy; z=cz-Math.cos(a)*distance; endGesture();
    }
    public static double clamp(double v,double low,double high) { return Math.max(low,Math.min(high,v)); }
    public static double angle(double v) { double a=v%360; return a>=180?a-360:a< -180?a+360:a; }
}
