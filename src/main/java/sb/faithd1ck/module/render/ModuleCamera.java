package sb.faithd1ck.module.render;

import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.MotionEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.value.ValueBoolean;
import sb.faithd1ck.value.ValueFloat;
import sb.faithd1ck.value.ValueInt;
import org.lwjgl.input.Mouse;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleCamera extends CheatModule {

	public ValueBoolean freelook = new ValueBoolean("FreeLook", false);
	public ValueBoolean viewclip = new ValueBoolean("ViewClip", false);
	public final ValueInt dist = new ValueInt("ClipDistance", 1, 0, 10).visible(()->viewclip.getValue());
	public ValueBoolean nohurtcam = new ValueBoolean("NoHurtCam", false);
	public ValueBoolean motionCamera = new ValueBoolean("MotionCamera", false);
	public ValueFloat motionLevel = new ValueFloat("MotionLevel", 30f, 0f, 50f).visible(()->motionCamera.getValue());


	private boolean released;

	public ModuleCamera() {
		super("Camera", Category.RENDER);
	}

	private final Handler<MotionEvent> motionEventHandler = event -> {
		if(event.getEventState() == MotionEvent.EventState.POST){
			if(freelook.getValue()) {
				if (Mouse.isButtonDown(2)) {
					mc.gameSettings.thirdPersonView = 1;
					released = false;
				} else {
					if (!released) {
						mc.gameSettings.thirdPersonView = 0;
						released = true;
					}
				}
			}
		}
	};

	public boolean getViewClip() {
		return viewclip.getValue();
	}

	public boolean getNoHurtCam() {
		return nohurtcam.getValue();
	}

	public boolean getMcLock() {
		return freelook.getValue();
	}
}
