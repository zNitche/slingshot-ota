package sh.znitche.slingshot;

import com.getcapacitor.Logger;

public class Slingshot {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
