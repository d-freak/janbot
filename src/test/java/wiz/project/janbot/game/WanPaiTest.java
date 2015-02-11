/**
 * WanPaiTest.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import wiz.project.jan.JanPai;



/**
 * WanPaiのテスト
 */
public final class WanPaiTest {
    
    /**
     * コンストラクタのテスト
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructor() {
        {
            // エラー (不正な牌数)
            final List<JanPai> sourceList = Arrays.asList(JanPai.TON, JanPai.TON);
            try {
                new WanPai(sourceList);
                fail();
            }
            catch (final IllegalArgumentException e) {
                assertEquals("Invalid source list size - 2", e.getMessage());
            }
        }
        {
            // エラー (牌リストがNull)
            final List<JanPai> sourceList = null;
            try {
                new WanPai(sourceList);
                fail();
            }
            catch (final NullPointerException e) {
                assertEquals("Source list is null.", e.getMessage());
            }
        }
    }
    
    /**
     * getDoraList() のテスト
     */
    @Test
    public void testGetDoraList() {
        {
            // 正常
            final WanPai wanPai = new WanPai(TEST_SOURCE_JANPAI_LIST);
            
            final List<JanPai> resultList = wanPai.getDoraList();
            
            assertEquals(1, resultList.size());
            for (final JanPai dora : resultList) {
                assertEquals(JanPai.NAN, dora);
            }
        }
        {
            // 正常 (カンドラ3枚追加)
            final WanPai wanPai = new WanPai(TEST_SOURCE_JANPAI_LIST);
            for (int i = 0; i < 3; i++) {
                wanPai.openNewDora();
            }
            
            final List<JanPai> resultList = wanPai.getDoraList();
            
            assertEquals(4, resultList.size());
            for (final JanPai dora : resultList) {
                assertEquals(JanPai.NAN, dora);
            }
        }
    }
    
    /**
     * getUraDoraList() のテスト
     */
    @Test
    public void testGetUraDoraList() {
        {
            // 正常
            final WanPai wanPai = new WanPai(TEST_SOURCE_JANPAI_LIST);
            
            final List<JanPai> resultList = wanPai.getUraDoraList();
            
            assertEquals(1, resultList.size());
            for (final JanPai dora : resultList) {
                assertEquals(JanPai.NAN, dora);
            }
        }
        {
            // 正常 (カンドラ3枚追加)
            final WanPai wanPai = new WanPai(TEST_SOURCE_JANPAI_LIST);
            for (int i = 0; i < 3; i++) {
                wanPai.openNewDora();
            }
            
            final List<JanPai> resultList = wanPai.getUraDoraList();
            
            assertEquals(4, resultList.size());
            for (final JanPai dora : resultList) {
                assertEquals(JanPai.NAN, dora);
            }
        }
    }
    
    /**
     * getWall() のテスト
     */
    @Test
    public void testGetWall() {
        {
            // 正常
            final WanPai wanPai = new WanPai(TEST_SOURCE_JANPAI_LIST);
            final JanPai wall = wanPai.getWall();
            assertEquals(JanPai.TON, wall);
        }
        {
            // エラー (5回目の嶺上ツモ)
            final WanPai wanPai = new WanPai(TEST_SOURCE_JANPAI_LIST);
            for (int i = 0; i < 4; i++) {
                wanPai.getWall();
            }
            
            try {
                wanPai.getWall();
                fail();
            }
            catch (final IndexOutOfBoundsException e) {}
        }
    }
    
    /**
     * isLimit() のテスト
     */
    @Test
    public void testIsLimit() {
        {
            // 正常
            final WanPai wanPai = new WanPai(TEST_SOURCE_JANPAI_LIST);
            
            assertFalse(wanPai.isLimit());
        }
        {
            // 正常 (4回嶺上ツモを実行)
            final WanPai wanPai = new WanPai(TEST_SOURCE_JANPAI_LIST);
            for (int i = 0; i < 4; i++) {
                wanPai.getWall();
            }
            
            assertTrue(wanPai.isLimit());
        }
    }
    
    /**
     * openNewDora() のテスト
     */
    @Test
    public void testOpenNewDora() {
        {
            // 正常
            final WanPai wanPai = new WanPai(TEST_SOURCE_JANPAI_LIST);
            
            final int beforeDoraCount = wanPai.getDoraList().size();
            wanPai.openNewDora();
            final int afterDoraCount = wanPai.getDoraList().size();
            
            assertEquals(beforeDoraCount + 1, afterDoraCount);
        }
        {
            // 正常 (4枚以上はめくれない)
            final WanPai wanPai = new WanPai(TEST_SOURCE_JANPAI_LIST);
            
            final int beforeDoraCount = wanPai.getDoraList().size();
            for (int i = 0; i < 10; i++) {
                wanPai.openNewDora();
            }
            final int afterDoraCount = wanPai.getDoraList().size();
            
            assertEquals(beforeDoraCount + 4, afterDoraCount);
        }
    }
    
    /**
     * reset() のテスト
     */
    @Test
    public void testReset() {
        {
            // 正常
            final WanPai wanPai = new WanPai(TEST_SOURCE_JANPAI_LIST);
            for (int i = 0; i < 4; i++) {
                wanPai.getWall();
                wanPai.openNewDora();
            }
            
            wanPai.reset();
            
            wanPai.getWall();  // 例外が発生しない
            assertEquals(1, wanPai.getDoraList().size());
        }
    }
    
    
    
    /**
     * 牌リスト
     */
    private static final List<JanPai> TEST_SOURCE_JANPAI_LIST =
        Collections.unmodifiableList(Arrays.asList(JanPai.TON, JanPai.TON, JanPai.TON,
                                                   JanPai.TON, JanPai.TON, JanPai.TON,
                                                   JanPai.TON, JanPai.TON, JanPai.TON,
                                                   JanPai.TON, JanPai.TON, JanPai.TON,
                                                   JanPai.TON, JanPai.TON));
    
}

