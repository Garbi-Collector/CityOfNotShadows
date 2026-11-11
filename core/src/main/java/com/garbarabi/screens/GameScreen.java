package com.garbarabi.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.garbarabi.Assets;
import com.garbarabi.CityOfNotShadowsGame;
import com.garbarabi.GameConfig;
import com.garbarabi.entities.Player;
import com.garbarabi.entities.PlayerProfile;
import com.garbarabi.input.PlayerInput;
import com.garbarabi.ui.DialogueBox;
import com.garbarabi.ui.PlayerMenu;
import com.garbarabi.world.CollisionSystem;
import com.garbarabi.world.GameMap;

/** Pantalla principal: mapa, jugador, diálogo inicial y menú del personaje. */
public class GameScreen extends ScreenAdapter {
    private static final float MAX_DELTA = 1f / 30f; // evita saltos grandes tras pausas o arrastres de ventana

    private final SpriteBatch batch;
    private final GameMap gameMap;
    private final CollisionSystem collisions;
    private final Player player;
    private final PlayerInput input = new PlayerInput();
    private final OrthographicCamera camera = new OrthographicCamera();  // sigue al jugador, en unidades de tile
    private final FitViewport viewport;                                  // mantiene la proporción del mundo al cambiar el tamaño
    private final OrthographicCamera uiCamera = new OrthographicCamera();  // cámara fija para la interfaz
    private final FitViewport uiViewport;
    private final PlayerProfile profile = new PlayerProfile();
    private final DialogueBox dialogue;
    private final PlayerMenu menu;
    private final Vector2 noMovement = new Vector2();  // movimiento nulo, usado mientras hay interfaz abierta

    public GameScreen(CityOfNotShadowsGame game) {
        this.batch = game.batch;

        TiledMap map = game.assets.getMap(Assets.MAP_TEST);
        this.gameMap = new GameMap(map, batch);
        float unitsPerPixel = 1f / gameMap.getTileSizePx();

        this.collisions = new CollisionSystem(map, unitsPerPixel);
        // Aparición sobre un tile floor_wood (columna 43, fila 67 en Tiled), alejado de los sólidos.
        this.player = new Player(game.assets, 43f, 12f, unitsPerPixel);

        // Viewport en unidades de tile: 320x240 px -> 20x15 tiles con tile de 16.
        float tile = gameMap.getTileSizePx();
        this.viewport = new FitViewport(GameConfig.VIRTUAL_WIDTH / tile, GameConfig.VIRTUAL_HEIGHT / tile, camera);

        // Interfaz en píxeles de sprite (escala UI_SCALE). Eje Y hacia arriba, el estándar de libGDX para fuentes.
        uiCamera.setToOrtho(false, GameConfig.UI_WIDTH, GameConfig.UI_HEIGHT);
        this.uiViewport = new FitViewport(GameConfig.UI_WIDTH, GameConfig.UI_HEIGHT, uiCamera);

        this.dialogue = new DialogueBox(game.assets.getMap(Assets.UI_BOX), game.font);
        this.menu = new PlayerMenu(game.assets.getMap(Assets.UI_BOX), game.assets, game.font, game.text, profile);
        dialogue.open(game.text.getArray("dialogue.intro"));  // el diálogo inicial se muestra al entrar
    }

    /** Un frame: procesa entrada, actualiza al jugador, dibuja el mapa y luego la interfaz encima. */
    @Override
    public void render(float delta) {
        float dt = Math.min(delta, MAX_DELTA);
        Vector2 movement = noMovement;
        // Mientras hay diálogo o menú abierto, el jugador no se mueve.
        if (dialogue.isActive()) {
            dialogue.update(dt, input);
        } else if (menu.isOpen()) {
            menu.update(dt, input);
        } else if (input.isMenuPressed()) {
            menu.open();
        } else {
            movement = input.readMovement();
        }
        player.update(dt, movement, collisions, gameMap.getWidthTiles(), gameMap.getHeightTiles());

        // Cámara fija sobre el jugador. El seguimiento suave llega en la Fase 2.
        camera.position.set(player.getCenterX(), player.getCenterY(), 0f);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();
        gameMap.render(camera);

        batch.begin();
        player.draw(batch);
        batch.end();

        // La interfaz se dibuja después, con su propia cámara, para que quede por encima del mapa.
        uiViewport.apply(true);
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        menu.draw(batch);
        dialogue.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        gameMap.dispose();
    }
}
