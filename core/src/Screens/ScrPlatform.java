//https://github.com/Mrgfhci/Drop
package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import java.util.Iterator;

public class ScrPlatform implements Screen, InputProcessor {

    DataStore data;
    Json json;
    JsonReader reader;
    FileHandle file;
    TextField text;
    Game game;
    SpriteBatch batch;
    Sprite sprBack, sprDinoAn;
    TextureRegion trTemp;
    Texture txDeadDino, txDino, txPlat, txSheet, txDinFor1, txDinoFor2, txBack1, txBack2, txJumpRight, txJumpLeft, txDead;
    SprDino sprDino;
    SprPlatform sprPlatform;
    int nAni = 0;
    int nScreenWid = Gdx.graphics.getWidth(), nDinoHei, nScreenX, nLevelCount = 1, fW, fH, fSx, fSy, nFrame, nPos, nHitType, HitPlatform;
    float fScreenWidth = Gdx.graphics.getWidth(), fScreenHei = Gdx.graphics.getHeight(), fDist, fVBackX, fProgBar = 0;
    float aspectratio = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
    private Array<SprPlatform> arsprPlatform;
    boolean isHitPlatform = false;
    OrthographicCamera camBack;
    ShapeRenderer shape = new ShapeRenderer();
    BitmapFont textFont, textFontLevel;
    String sLevel = "Level " + nLevelCount;
    private float fVy;
    private float fVx;

    public ScrPlatform(Game _game) {
        data = new DataStore();
        json = new Json();
        file = new FileHandle("myjson2.json");
        reader = new JsonReader();
        nHitType = 0;
        HitPlatform = 0;
        SetFont();
        game = _game;
        txDinFor1 = (new Texture(Gdx.files.internal("0.png")));
        txDinoFor2 = (new Texture(Gdx.files.internal("1.png")));
        txBack1 = (new Texture(Gdx.files.internal("3.png")));
        txBack2 = (new Texture(Gdx.files.internal("4.png")));
        txJumpRight = (new Texture(Gdx.files.internal("2.png")));
        txJumpLeft = (new Texture(Gdx.files.internal("5.png")));
        txDead = (new Texture(Gdx.files.internal("6.png")));
        batch = new SpriteBatch();
        txDino = new Texture("Dinosaur.png");
        txDeadDino = new Texture("dead.png");
        txPlat = new Texture("Platform.png");
        sprBack = new Sprite(new Texture(Gdx.files.internal("world.jpg")));
        sprBack.setSize(fScreenWidth, fScreenHei);
        camBack = new OrthographicCamera(fScreenWidth /**
                 * aspectratio
                 */
                , fScreenHei);
        camBack.position.set(fScreenWidth / 2, fScreenHei / 2, 0);
        Gdx.input.setInputProcessor((this));
        Gdx.graphics.setWindowedMode(800, 500);
        sprDino = new SprDino(txDinFor1);
        sprPlatform = new SprPlatform(txPlat);
        arsprPlatform = new Array<SprPlatform>();
        arsprPlatform.add(sprPlatform);
    }

    public void SetFont() {
        FileHandle fontFile = Gdx.files.internal("Woods.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 25;
        parameter.color = Color.BLACK;
        textFont = generator.generateFont(parameter);
        parameter.size = 45;
        textFontLevel = generator.generateFont(parameter);

        generator.dispose();
    }

    @Override
    public void show() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void render(float f) {
        Gdx.gl.glClearColor(1, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camBack.update();
        sprDino.PositionSet();
        HitDetection();
        sprDino.HitDetectionBounds(camBack.viewportWidth);
        sprDino.gravity();
        nFrame++;
        if (nFrame > 7) {
            nFrame = 0;
        }
        for (SprPlatform sprPlatform : arsprPlatform) {
            sprPlatform.update();
        }
        float fCounter = 0;
        /*while(Gdx.input.isKeyPressed(Input.Keys.D)){
         sprDino.Animate(txDinoFor2);
         fCounter += .25;
         if(fCounter == 2){
         sprDino.Animate(txDinFor1);
         fCounter = 0;
         }
         }*/
        if (nAni == 0) {
            sprDino.Animate(txDinFor1);
        } else if (nAni == 1) {
            sprDino.Animate(txDinoFor2);
        } else if (nAni == 2) {
            sprDino.Animate(txBack2);
        } else if (nAni == 5) {
            sprDino.Animate(txBack1);
        } else if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && Gdx.input.isKeyPressed(Input.Keys.D)) {
            sprDino.Animate(txJumpRight);
        } else if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && Gdx.input.isKeyPressed(Input.Keys.A)) {
            sprDino.Animate(txJumpLeft);
        }
        sprDino.update();
        batch.begin();
        if ((nScreenX < -Gdx.graphics.getWidth() || nScreenX > Gdx.graphics.getWidth())) {
            nScreenX = 0;
        }
        batch.setProjectionMatrix(camBack.combined);
        batch.draw(sprBack, nScreenX, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(sprBack, nScreenX - Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(sprBack, nScreenX + Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        textFontLevel.draw(batch, sLevel, fScreenWidth / 6, (fScreenHei / 10) * 9);
        batch.draw(sprDino.getSprite(), sprDino.getX(), sprDino.getY());
        for (SprPlatform sprPlatform : arsprPlatform) {
            batch.draw(sprPlatform.getSprite(), sprPlatform.getX(), sprPlatform.getY());
        }
        if (sprBack.getX() > 0) {
            nScreenX += fVx;
        }
        nScreenX -= fVx;
        batch.end();
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BLACK);
        shape.rect((fScreenWidth - (fScreenWidth / 3) * 2) / 2, fScreenHei / 120, (fScreenWidth / 3) * 2, fScreenWidth / 40);
        shape.setColor(Color.WHITE);
        shape.rect((fScreenWidth - (fScreenWidth / 3) * 2) / 2, fScreenHei / 120, fProgBar, fScreenWidth / 40);
        shape.end();
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (fProgBar <= 0) {
                fProgBar = 0;

            } else {
                fProgBar -= .7;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            fProgBar += .7;
        }
        if (fProgBar >= ((fScreenWidth / 3) * 2)) {
            nLevelCount++;
            fProgBar = 0;
            sLevel = "Level " + nLevelCount;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            data.sInput = sLevel;
            json.toJson(data, file);
            System.out.println(reader.parse(file).get("sInput").asString());
            Gdx.app.exit();
            dispose();
        }
        Iterator<SprPlatform> iter = arsprPlatform.iterator();
        while (iter.hasNext()) {
            SprPlatform sprPlatform = iter.next();
            if (sprPlatform.canSpawn() && (arsprPlatform.size < 2)) {
                sprPlatform = new SprPlatform(txPlat);
                arsprPlatform.add(sprPlatform);
            }
            if (sprPlatform.isOffScreen()) {
                iter.remove();
            }
        }
    }

    void SpawnPlatform() {
        Iterator<SprPlatform> iter = arsprPlatform.iterator();
        while (iter.hasNext()) {
            SprPlatform sprPlatform = iter.next();
            if (sprPlatform.canSpawn() && (arsprPlatform.size < 2)) {
                sprPlatform = new SprPlatform(txPlat);
                arsprPlatform.add(sprPlatform);
            }
            if (sprPlatform.isOffScreen()) {
                iter.remove();
            }
        }
    }

    void HitDetection() {
        nHitType = HitPlatform();
        if (nHitType == 0) {
            System.out.println("NO HIT");
            sprDino.bPlatformCarry = false;
            sprDino.fGround = 0;
            sprDino.bGrav = true;
            sprDino.bGoThrough = false;
            if (sprDino.bJump == false) {
                sprDino.vGrav.set(0, (float) -0.4);
            }
        } else if (nHitType == 1) {
            sprDino.bGoThrough = false;
            System.out.println("dead");
        } else if (nHitType == 2) {
            sprDino.bGoThrough = false;
            if (sprDino.bGrav && sprDino.vDir.x < 0) {
                sprDino.bJump = false;
                sprDino.bGrav = false;
            }
            if (sprDino.bMove == false && sprDino.bGrav == false) {
                sprDino.bPlatformCarry = true;
            }
            sprDino.fGround = sprPlatform.vPrevPos.y + sprPlatform.getSprite().getHeight() - 1;
            sprDino.vPos.y = sprDino.fGround;
            System.out.println("land");
        } else if (nHitType == 3) {
            sprDino.bGoThrough = true;
            System.out.println("pass through");
        } else if (nHitType == 4) {
            sprDino.bGoThrough = false;
            System.out.println("I'm on the ground and the block hit me");
        }
    }

    int HitPlatform() {
        Iterator<SprPlatform> iter = arsprPlatform.iterator();
        while (iter.hasNext()) {
            SprPlatform sprPlatform = iter.next();
            if (sprDino.getSprite().getBoundingRectangle().overlaps(sprPlatform.getSprite().getBoundingRectangle())) {
                if (sprDino.vPrevPos.y >= (sprPlatform.vPrevPos.y + sprPlatform.getSprite().getHeight())) {
                    return 2;
                } else if (sprDino.vPos.y == sprPlatform.vPrevPos.y + sprPlatform.getSprite().getHeight() - 1) {
                    return 2;
                } else if (sprDino.vPrevPos.y + sprDino.getSprite().getHeight() <= sprPlatform.vPrevPos.y) {
                    return 3;
                } else if (sprDino.bGrav && sprDino.bGoThrough == false) {
                    return 1;
                } else if (sprDino.bGoThrough == true) {
                    return 3;
                } else if (sprDino.bGrav == false) {
                    return 4;
                }
            }
        }
        return 0;
    }

    @Override
    public void resize(int i, int i1) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void pause() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resume() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void hide() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dispose() {
        sprPlatform.getSprite().getTexture().dispose();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE && sprDino.bJump == false) {
            sprDino.vPos.add(0, 1);
            sprDino.vDir.set((float) sprDino.vDir.x, 25);
            sprDino.vGrav.set(0, (float) -0.5);
            sprDino.bJump = true;
            sprDino.bGrav = true;
            nAni = 3;
        } else if (keycode == Input.Keys.A) {
            sprDino.bMove = true;
            sprDino.vDir.set(-2, (float) sprDino.vDir.y);
            fVx = -2;
            nAni = 2;
        } else if (keycode == Input.Keys.D) {
            sprDino.bMove = true;
            sprDino.vDir.set(2, (float) sprDino.vDir.y);
            fVx = 2;
            nAni = 1;
        } else if (keycode == Input.Keys.ESCAPE) {
            //System.exit(3);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.A) {
            sprDino.bMove = false;
            sprDino.vDir.set(0, (float) sprDino.vDir.y);
            fVx = 0;
            nAni = 5;
        } else if (keycode == Input.Keys.D) {
            sprDino.bMove = false;
            sprDino.vDir.set(0, (float) sprDino.vDir.y);
            fVx = 0;
            nAni = 0;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean scrolled(int i) {
        return false;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
