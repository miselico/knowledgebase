package miselico.prototypes.joiners;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ IntersectRemTest.class, IntersectAddTest.class, UnionAddTest.class, UnionRemTest.class })
public class AllTests {

}
