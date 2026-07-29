package io.github.tenite5.pvpclient.client.core.setting;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFW;

public class KeySetting extends Setting {
	/** Mouse button b is stored as MOUSE_BASE - b so it can never collide with GLFW key codes (>= 0). */
	private static final int MOUSE_BASE = -100;

	private int key;
	private final int defaultKey;

	public KeySetting(String id, String name, int defaultGlfwKey) {
		super(id, name);
		this.key = defaultGlfwKey;
		this.defaultKey = defaultGlfwKey;
	}

	public int get() {
		return this.key;
	}

	public void set(int code) {
		this.key = code;
	}

	public void reset() {
		this.key = this.defaultKey;
	}

	public boolean isDown(Window window) {
		return isCodeDown(window, this.key);
	}

	public String display() {
		return displayOf(this.key);
	}

	public static int fromMouseButton(int button) {
		return MOUSE_BASE - button;
	}

	public static boolean isMouse(int code) {
		return code <= MOUSE_BASE;
	}

	public static boolean isBound(int code) {
		return code >= 0 || isMouse(code);
	}

	public static boolean isCodeDown(Window window, int code) {
		if (isMouse(code)) {
			return GLFW.glfwGetMouseButton(window.getWindow(), MOUSE_BASE - code) == GLFW.GLFW_PRESS;
		}
		return code >= 0 && InputConstants.isKeyDown(window.getWindow(), code);
	}

	public static String displayOf(int code) {
		if (isMouse(code)) {
			return InputConstants.Type.MOUSE.getOrCreate(MOUSE_BASE - code).getDisplayName().getString();
		}
		if (code < 0) {
			return "None";
		}
		return InputConstants.Type.KEYSYM.getOrCreate(code).getDisplayName().getString();
	}
}
