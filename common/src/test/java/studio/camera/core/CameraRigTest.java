package studio.camera.core;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class CameraRigTest {
    private CameraRig rig(){var c=new CameraRig();c.begin(0,0,0,0,0,70);return c;}
    @Test void movementIndependentOfPitch(){var c=rig();c.pitch=85;c.move(1,0,0,0,0,false,.1);assertEquals(.5,c.z,1e-9);assertEquals(0,c.y);}
    @Test void diagonalSpeedNormalized(){var c=rig();c.move(1,1,1,0,0,false,.1);assertEquals(.5,Math.sqrt(c.x*c.x+c.y*c.y+c.z*c.z),1e-9);}
    @Test void rollTransformsPanning(){var c=rig();c.roll=90;c.pan(-100,0);assertEquals(0,c.x,1e-9);assertTrue(c.y>0);}
    @Test void undoIncludesWholeDrag(){var c=rig();c.beginGesture();for(int i=0;i<30;i++)c.pan(1,0);c.endGesture();assertEquals(1,c.undoCount());assertTrue(c.undo());assertEquals(0,c.x);}
    @Test void undoBoundedToTen(){var c=rig();for(int i=0;i<15;i++){c.beginGesture();c.x++;c.endGesture();}assertEquals(10,c.undoCount());for(int i=0;i<10;i++)assertTrue(c.undo());assertEquals(5,c.x);assertFalse(c.undo());}
    @Test void noOpDoesNotUseUndo(){var c=rig();c.beginGesture();c.endGesture();assertEquals(0,c.undoCount());}
    @Test void invalidApplyIsAtomic(){var c=rig();var old=c.snapshot();assertFalse(c.apply(new double[]{1,2,3,0,91,0,70,5}));assertEquals(old,c.snapshot());assertFalse(c.apply(new double[]{1,2,Double.NaN,0,0,0,70,5}));assertEquals(old,c.snapshot());}
    @Test void limitsAndAngleWrap(){var c=rig();c.look(4000,10000);assertEquals(-120,c.yaw);assertEquals(90,c.pitch);c.zoom(1000);assertEquals(10,c.fov);c.zoom(-1000);assertEquals(150,c.fov);}
    @Test void presetsFrameSamePoint(){var c=rig();for(int side=0;side<4;side++){c.preset(3,4,5,0,side,1.8,.6,1);double dx=3-c.x,dz=5-c.z;assertEquals(Math.toRadians(c.yaw),Math.atan2(-dx,dz),1e-9);assertEquals(4,c.y);assertEquals(0,c.roll);}}
    @Test void resetDropsOldSessionUndo(){var c=rig();c.beginGesture();c.x=10;c.endGesture();c.begin(2,3,4,0,0,70);assertEquals(0,c.undoCount());assertEquals(2,c.x);}
}
