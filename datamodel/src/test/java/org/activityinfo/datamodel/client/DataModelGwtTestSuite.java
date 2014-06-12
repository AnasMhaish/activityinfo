package org.activityinfo.datamodel.client;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestCase;
import org.activityinfo.datamodel.client.resty.RestyTest;


public class DataModelGwtTestSuite extends TestCase {

    public static Test suite()
    {
        GWTTestSuite suite = new GWTTestSuite("DataModel Tests");
//        suite.addTestSuite( GwtTestInstance.class );
//        suite.addTestSuite( GwtAutoTest.class );
        suite.addTestSuite( RestyTest.class );
        return suite;
    }
}
