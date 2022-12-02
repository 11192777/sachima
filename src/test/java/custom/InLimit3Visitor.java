package custom;

import org.zaizai.sachima.sql.dialect.oracle.visitor.InLimit1000Visitor;



/**
 * <H1></H1>
 *
 * @author Qingyu.Meng
 * @version 1.0
 * @date 2022/11/16 18:58
 */
public class InLimit3Visitor extends InLimit1000Visitor {

    public static InLimit1000Visitor getInstance() {
        return INSTANCE == null ? new InLimit3Visitor() : INSTANCE;
    }

    @Override
    public int getInItemLimit() {
        return 3;
    }
}
