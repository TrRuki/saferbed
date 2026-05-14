package com.trruki.saferbed.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class SaferbedClient implements ClientModInitializer {
    public static boolean enabled = true;
    public static KeyMapping toggleKey;

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("saferbed.properties");

    public static void saveConfig() {
        Properties props = new Properties();
        props.setProperty("enabled", String.valueOf(enabled));
        try (var writer = Files.newBufferedWriter(CONFIG_PATH)) {
            props.store(writer, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig() {
        if (!Files.exists(CONFIG_PATH)) return;
        Properties props = new Properties();
        try (var reader = Files.newBufferedReader(CONFIG_PATH)) {
            props.load(reader);
            enabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitializeClient() {
        loadConfig();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.saferbed.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_0,
                new KeyMapping.Category(Identifier.parse("saferbed"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (toggleKey.consumeClick()) {
                enabled = !enabled;
                saveConfig();

                minecraft.gui.setOverlayMessage(Component.literal(enabled ? "§aSafer Bed: ON" : "§cSafer Bed: OFF"), false);
            }
        });
    }
}
