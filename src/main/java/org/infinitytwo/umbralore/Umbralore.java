package org.infinitytwo.umbralore;

import org.infinitytwo.umbralore.block.BedrockBlockType;
import org.infinitytwo.umbralore.block.DirtBlockType;
import org.infinitytwo.umbralore.block.GrassBlockType;
import org.infinitytwo.umbralore.block.StoneBlockType;
import org.infinitytwo.umbralore.constants.ResourceType;
import org.infinitytwo.umbralore.debug.Main;
import org.infinitytwo.umbralore.exception.UnknownRegistryException;
import org.infinitytwo.umbralore.model.TextureAtlas;
import org.infinitytwo.umbralore.registry.BlockRegistry;
import org.infinitytwo.umbralore.registry.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;


//        registry.register(new GrassBlockType());
//        registry.register(new DirtBlockType());
//        registry.register(new StoneBlockType());
//        registry.register(new BedrockBlockType());
//
//        reader = new BlockDataReader(registry);
//
//        overworld = new Overworld(486486,registry);
//
//        shaderProgram = new ShaderProgram(
//                """
//                        #version 330 core
//                        layout (location = 0) in vec3 aPos;
//                        layout (location = 1) in vec2 aTexCoord;
//                        layout (location = 2) in float aBrightness;
//
//                        out vec2 TexCoord;
//                        out float Brightness;
//
//                        uniform mat4 model;
//                        uniform mat4 view;
//                        uniform mat4 projection;
//
//                        void main() {
//                            TexCoord = aTexCoord;
//                            Brightness = aBrightness;
//                            gl_Position = projection * view * model * vec4(aPos, 1.0);
//                        }
//                        """,
//                """
//                        #version 330 core
//                        in vec2 TexCoord;
//                        in float Brightness;
//
//                        uniform sampler2D ourTexture;
//
//                        out vec4 FragColor;
//
//                        void main() {
//                            vec4 texColor = texture(ourTexture, TexCoord);
//                            FragColor = vec4(texColor.rgb * Brightness, texColor.a);
//                        }
//                        """
//        );
//
//        atlas = new TextureAtlas(2, 5);
//        try {
//            atlas.addTexture("src/main/resources/dirt.png");
//            atlas.addTexture("src/main/resources/stone.png");
//            atlas.addTexture("src/main/resources/pick.png");
//            atlas.addTexture("src/main/resources/grass_side.png");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        atlas.build();
//
//        // FIX: Client does not need to generate chunks itself. It gets them from the server.
//        // test = new ProcedureGridMap(shaderProgram, atlas, 5, new Overworld(1561, registry).settings, registry);
//
//        env = new Environment();
//        serverThread = new ServerThread(84653);
//
//        try {
//            networkThread = new ClientNetworkThread(eventBus, 5000, "localhost", 5555);
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        }
//
//
public class Umbralore {
    private static TextureAtlas atlas;
    private static final String name = "umbralore";

    public static void construction() {
        atlas = new TextureAtlas(25,150);

        ResourceLocation.registerMod(name);
        ResourceLocation.registerKey(name,"grass_block", Path.of("src/main/resources/grass_side.png"), ResourceType.TEXTURE);
        ResourceLocation.registerKey(name,"dirt", Path.of("src/main/resources/dirt.png"), ResourceType.TEXTURE);
        ResourceLocation.registerKey(name,"stone", Path.of("src/main/resources/stone.png"), ResourceType.TEXTURE);
        ResourceLocation.registerKey(name,"bedrock", Path.of("src/main/resources/pick.png"), ResourceType.TEXTURE);

        try {
            GrassBlockType grass = new GrassBlockType(atlas.addTexture(ResourceLocation.getPathFromString(name,"grass_block").toAbsolutePath().toString()));
            DirtBlockType dirt = new DirtBlockType(atlas.addTexture(ResourceLocation.getPathFromString(name,"dirt").toAbsolutePath().toString()));
            StoneBlockType stone = new StoneBlockType(atlas.addTexture(ResourceLocation.getPathFromString(name,"stone").toAbsolutePath().toString()));
            BedrockBlockType bedrock = new BedrockBlockType(atlas.addTexture(ResourceLocation.getPathFromString(name,"bedrock").toAbsolutePath().toString()));

            BlockRegistry registry = Main.getBlockRegistry();
            registry.register(dirt);
            registry.register(stone);
            registry.register(bedrock);
            registry.register(grass);

        } catch (UnknownRegistryException | IOException e) {
            throw new RuntimeException(e);
        }

        atlas.build();
    }

    public static void init() {

    }

    public static void loop() {

    }

    public static void cleanup() {

    }
}
