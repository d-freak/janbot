/**
 * GameMasterTest.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import wiz.project.janbot.MockBOT;
import wiz.project.janbot.game.exception.JanException;



/**
 * GameMasterのテスト
 */
public final class GameMasterTest {
    
    /**
     * getStatus() のテスト
     */
    @Test
    public void testGetStatus() {
        {
            // 正常 (デフォルト値)
            assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
        }
    }
    
    /**
     * onDiscard() のテスト
     */
    @Test
    public void testOnDiscard() throws JanException, IOException {
        {
            // 正常
            MockBOT.initialize();
            MockBOT.connect();
            
            try {
                GameMaster.getInstance().onStartSolo(TEST_PLAYER_NAME);
                GameMaster.getInstance().onDiscard();
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
    }
    
    /**
     * onEnd() のテスト
     */
    @Test
    public void testOnEnd() throws JanException, IOException {
        {
            // 正常
            MockBOT.initialize();
            MockBOT.connect();
            
            try {
                GameMaster.getInstance().onStartSolo(TEST_PLAYER_NAME);
            }
            finally {
                GameMaster.getInstance().onEnd();
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
        }
    }
    
    /**
     * onStartSolo() のテスト
     */
    @Test
    public void testOnStartSolo() throws JanException, IOException {
        {
            // 正常
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            try {
                GameMaster.getInstance().onStartSolo(playerName);
                assertEquals(GameStatus.PLAYING_SOLO, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // 正常 (開始済み)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = TEST_PLAYER_NAME;
            try {
                GameMaster.getInstance().onStartSolo(playerName);
                GameMaster.getInstance().onStartSolo(playerName);
                assertEquals(GameStatus.PLAYING_SOLO, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // エラー (空のプレイヤー名)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = "";
            try {
                GameMaster.getInstance().onStartSolo(playerName);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Player name is empty.", e.getMessage());
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
        {
            // エラー (プレイヤー名がNull)
            MockBOT.initialize();
            MockBOT.connect();
            
            final String playerName = null;
            try {
                GameMaster.getInstance().onStartSolo(playerName);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Player name is null.", e.getMessage());
                assertEquals(GameStatus.IDLE, GameMaster.getInstance().getStatus());
            }
            finally {
                GameMaster.getInstance().onEnd();
            }
        }
    }
    
    
    
    /**
     * プレイヤー名
     */
    private static final String TEST_PLAYER_NAME = "テストプレイヤー";
    
}

