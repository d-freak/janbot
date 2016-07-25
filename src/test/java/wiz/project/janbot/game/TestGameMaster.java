/**
 * GameMaster.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wiz.io.serializer.Serializer;
import wiz.project.ircbot.IRCBOT;
import wiz.project.jan.JanPai;
import wiz.project.jan.Wind;
import wiz.project.janbot.game.exception.JanException;



/**
 * ゲーム管理
 */
public final class TestGameMaster extends GameMaster {
    
    /**
     * コンストラクタを自分自身に限定許可
     */
    private TestGameMaster() {
    }
    
    
    
    /**
     * インスタンスを取得
     * 
     * @return インスタンス。
     */
    public static TestGameMaster getInstance() {
        return INSTANCE;
    }
    
    
    
    /**
     * リプレイ処理 (中国麻雀)
     * 
     * @param playerName プレイヤー名。
     * @throws JanException ゲーム処理エラー。
     * @throws IOException ファイル入出力に失敗。
     */
    @SuppressWarnings("unchecked")
    public void onTestChm(final String playerName) throws JanException, IOException {
        if (playerName == null) {
            throw new NullPointerException("Player name is null.");
        }
        if (playerName.isEmpty()) {
            throw new IllegalArgumentException("Player name is empty.");
        }
        
        // 開始済み判定
        synchronized (_STATUS_LOCK) {
            if (!_status.isIdle()) {
                IRCBOT.getInstance().println("--- Already started ---");
                return;
            }
            _status = GameStatus.PLAYING_SOLO;
        }
        
        if (!Files.exists(Paths.get(DECK_SAVE_PATH)) ||
            !Files.exists(Paths.get(PLAYER_TABLE_SAVE_PATH))) {
            IRCBOT.getInstance().println("--- Test data is not found ---");
            return;
        }
        
        // 牌山と席順をロード
        final List<JanPai> deck = (List<JanPai>)Serializer.read(DECK_SAVE_PATH);
        final Map<Wind, Player> playerTable = (Map<Wind, Player>)Serializer.read(PLAYER_TABLE_SAVE_PATH);
        
        // プレイヤー名を差し替え
        final Wind playerWind = getPlayerWind(playerTable);
        playerTable.put(playerWind, new Player(playerName, PlayerType.HUMAN));
        
        // ゲーム開始
        synchronized (_CONTROLLER_LOCK) {
            _controller = createChmJanController(true);
            _controller.start(deck, new ArrayList<Integer>(), playerTable);
        }
    }
    
    
    
    /**
     * 自分自身のインスタンス
     */
    private static final TestGameMaster INSTANCE = new TestGameMaster();
    
    /**
     * 保存パス
     */
    private static final String DECK_SAVE_PATH         = "./test/deck.bin";
    private static final String PLAYER_TABLE_SAVE_PATH = "./test/player_table.bin";
}

