package cn.ifafu.ifafu;

import org.junit.Test;

import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.util.GlobalLib;

public class UnitTest2 {

    boolean b = false;


    @Test
    public void main() {
        System.out.println(GlobalLib.formatFloat(getF(), 2));
    }

    public float getF() {
        Score s = new Score();
        s.setCredit(0F);
        return 0 / s.getGpa();
    }
}
