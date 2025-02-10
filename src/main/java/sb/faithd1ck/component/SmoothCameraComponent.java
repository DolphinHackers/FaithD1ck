package sb.faithd1ck.component;

import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.Listener;
import sb.faithd1ck.event.impl.MotionEvent;
import sb.faithd1ck.utils.MSTimer;

import static sb.faithd1ck.utils.IMinecraft.mc;

public class SmoothCameraComponent implements Listener {

    public static double y;
    public static MSTimer stopWatch = new MSTimer();
    public static SmoothCameraComponent INSTANCE;

    public SmoothCameraComponent() {
        INSTANCE = this;
    }

    public static void setY(double y) {
        stopWatch.reset();
        SmoothCameraComponent.y = y;
    }

    public static void setY() {
        if (stopWatch.check(60)) SmoothCameraComponent.y = mc.thePlayer.lastTickPosY;
        stopWatch.reset();
    }

    public final Handler<MotionEvent> onPreMotion = event -> {
        if (event.isPost()) return;
        if (stopWatch.check(60)) return;
        mc.thePlayer.cameraYaw = 0;
        mc.thePlayer.cameraPitch = 0;
    };

    @Override
    public boolean isAccessible() {
        return true;
    }
}
