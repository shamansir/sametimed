package name.shamansir.sametimed.test;

import name.shamansir.sametimed.wave.doc.TagID;

import org.junit.Assert;
import org.junit.Test;

// TODO: run tests from ant build file

public class TestTagIDGeneration {
	
	@Test
	public void testAssigningValues() {
		TagID tagID = new TagID(null);
		Assert.assertEquals(null, tagID.getValue());
		tagID = new TagID("a");
		Assert.assertEquals("a", tagID.getValue());
		tagID = new TagID("x");
		Assert.assertEquals("x", tagID.getValue());
		Assert.assertFalse("X".equals(tagID.getValue()));
	}
	
	@Test
	public void testFirstTagOk() {
		Assert.assertEquals("a", new TagID(null).makeNext().getValue());
	}	
	
	@Test
	public void testSingleDomainLetterIncrease() {
		TagID nothing = new TagID(null);
		Assert.assertEquals(-1,  nothing.intValue());
		TagID first = nothing.makeNext();
		Assert.assertEquals("a", first.getValue());
		Assert.assertEquals( 0,  first.intValue());
		Assert.assertEquals("b", first.makeNext().getValue());
		Assert.assertEquals( 1,  first.makeNext().intValue());
		Assert.assertEquals("c", first.makeNext().makeNext().getValue());
		Assert.assertEquals( 2,  first.makeNext().makeNext().intValue());
		TagID last = first;		
		for (int i = 0; i < 9; i++) { // scroll to 'j'
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 9 , last.intValue());
		Assert.assertEquals("j", last.getValue());
		for (int i = 0; i < 10; i++) { // scroll to 't'
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 19, last.intValue());
		Assert.assertEquals("t", last.getValue());
		for (int i = 0; i < 4; i++) { // scroll to 'x'
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 23, last.intValue());
		Assert.assertEquals("x", last.getValue());		
		for (int i = 0; i < 2; i++) { // scroll to 'z'
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 25, last.intValue());
		Assert.assertEquals("z", last.getValue());
		last = last.makeNext();
		Assert.assertNotNull(last);
		Assert.assertEquals( 26, last.intValue());
		Assert.assertEquals("A", last.getValue());
		for (int i = 0; i < 6; i++) { // scroll to 'G'
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 32, last.intValue());
		Assert.assertEquals("G", last.getValue());
		for (int i = 0; i < 6; i++) { // scroll to 'M'
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 38, last.intValue());
		Assert.assertEquals("M", last.getValue());
		for (int i = 0; i < 6; i++) { // scroll to 'S'
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 44, last.intValue());
		Assert.assertEquals("S", last.getValue());
		for (int i = 0; i < 5; i++) { // scroll to 'X'
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 49, last.intValue());
		Assert.assertEquals("X", last.getValue());
		for (int i = 0; i < 2; i++) { // scroll to 'Z'
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 51, last.intValue());
		Assert.assertEquals("Z", last.getValue());		
	}
	
	@Test	
	public void testSingleDomainLettersSequencesIncrease() {
		// Works long, be patient
		TagID nothing = new TagID(null);
		Assert.assertNotNull(nothing);
		Assert.assertEquals(-1, nothing.intValue());
		TagID last = nothing;
		for (int i = 0; i < 52; i++) { // scroll to 'Z'
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 51, last.intValue());
		Assert.assertEquals("Z", last.getValue()); // 51
		last = last.makeNext();
		Assert.assertNotNull(last);
		Assert.assertEquals( 52 , last.intValue());
		Assert.assertEquals("aa", last.getValue());	// 1 * 52 + 0 = 52	
		last = last.makeNext();
		Assert.assertNotNull(last);
		Assert.assertEquals( 53 , last.intValue());
		Assert.assertEquals("ab", last.getValue()); // 1 * 52 + 1 = 53
		for (int i = 0; i < 9; i++) { // scroll to 'ak' //  (62 = 1 * 52 + 10) - 53  
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 62 , last.intValue());
		Assert.assertEquals("ak", last.getValue());
		for (int i = 0; i < 22; i++) { // scroll to 'aG' //  (84 = 1 * 52 + 32) - 62
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 84 , last.intValue());
		Assert.assertEquals("aG", last.getValue());
		for (int i = 0; i < 57; i++) { // scroll to 'bL' // (141 = 2 * 52 + 37) - 84
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals( 141, last.intValue());
		Assert.assertEquals("bL", last.getValue());
		for (int i = 0; i < 1262; i++) { // scroll to 'zZ' // (1403 = 26 * 52 + 51) - 141
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals(1403, last.intValue());
		Assert.assertEquals("zZ", last.getValue());
		last = last.makeNext();
		Assert.assertNotNull(last);
		Assert.assertEquals(1404, last.intValue());
		Assert.assertEquals("Aa", last.getValue()); // (1404 = 27 * 52 + 0)
		for (int i = 0; i < 7; i++) { // scroll to 'Ah' // (1411 = 27 * 52 + 7) - 1404
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals(1411, last.intValue());
		Assert.assertEquals("Ah", last.getValue());
		for (int i = 0; i < 26; i++) { // scroll to 'AH' // (1437 = 27 * 52 + 33) - 1411
			last = last.makeNext();
		}		
		Assert.assertNotNull(last);
		Assert.assertEquals(1437, last.intValue());
		Assert.assertEquals("AH", last.getValue());		
		for (int i = 0; i < 48; i++) { // scroll to 'BD' // (1485 = 28 * 52 + 29) - 1437
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals(1485, last.intValue());
		Assert.assertEquals("BD", last.getValue());
		for (int i = 0; i < 1121; i++) { // scroll to 'Xg' // (2606 = 50 * 52 + 6) - 1485
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals(2606, last.intValue());
		Assert.assertEquals("Xg", last.getValue());
		for (int i = 0; i < 77; i++) { // scroll to 'YF' // (2683 = 51 * 52 + 31) - 2606
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals(2683, last.intValue());
		Assert.assertEquals("YF", last.getValue());
		for (int i = 0; i < 72; i++) { // scroll to 'ZZ' // (2755 = 52 * 52 + 51) - 2683
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals(2755, last.intValue());
		Assert.assertEquals("ZZ", last.getValue());
		last = last.makeNext();
		Assert.assertNotNull(last);
		Assert.assertEquals( 2756, last.intValue());
		Assert.assertEquals("aaa", last.getValue()); // (2756 = (1 * 52 * 52) + (1 * 52) + 0)
		for (int i = 0; i < 56084963; i++) { // scroll to 'gHTyZ' // (56087719 = (7 * 52^4) + (34 * 52^3) + (46 * 52^2) + (25 * 52) + 51) - 2756
			last = last.makeNext();
		}
		Assert.assertNotNull(last);
		Assert.assertEquals(56087719, last.intValue());
		Assert.assertEquals( "gHTyZ", last.getValue());
		last = last.makeNext();
		Assert.assertNotNull(last);
		Assert.assertEquals(56087720, last.intValue());
		Assert.assertEquals( "gHTza", last.getValue()); // (56087720 = (7 * 52^4) + (34 * 52^3) + (46 * 52^2) + (26 * 52) + 0)
		
	}
	
	@Test	
	public void testMultipleDomains() {
		TagID nothing = new TagID(null);
		TagID first = nothing.makeNext();
		
		TagID firstChildOfFirst = first.makeForFirstChild();
		Assert.assertNotNull(firstChildOfFirst);
		Assert.assertEquals("a.a", firstChildOfFirst.getValue());
		Assert.assertEquals(0, firstChildOfFirst.intValue());
		TagID secondChildOfFirst = firstChildOfFirst.makeNext();
		Assert.assertNotNull(secondChildOfFirst);
		Assert.assertEquals("a.b", secondChildOfFirst.getValue());
		Assert.assertEquals(1, secondChildOfFirst.intValue());
		TagID thirdChildOfFirst = secondChildOfFirst.makeNext();
		Assert.assertNotNull(thirdChildOfFirst);
		Assert.assertEquals("a.c", thirdChildOfFirst.getValue());
		Assert.assertEquals(2, thirdChildOfFirst.intValue());
		TagID fourth = first.makeNext().makeNext().makeNext();
		
		Assert.assertNotNull(fourth);
		Assert.assertEquals("d", fourth.getValue());
		TagID firstChildOfFourth = fourth.makeForFirstChild();
		Assert.assertNotNull(firstChildOfFourth);
		Assert.assertEquals("d.a", firstChildOfFourth.getValue());
		Assert.assertEquals(0, firstChildOfFourth.intValue());
		TagID secondChildOfFourth = firstChildOfFourth.makeNext();
		Assert.assertNotNull(secondChildOfFourth);
		Assert.assertEquals("d.b", secondChildOfFourth.getValue());
		Assert.assertEquals(1, secondChildOfFourth.intValue());
		TagID thirdChildOfFourth = secondChildOfFourth.makeNext();
		Assert.assertNotNull(thirdChildOfFourth);
		Assert.assertEquals("d.c", thirdChildOfFourth.getValue());
		Assert.assertEquals(2, thirdChildOfFourth.intValue());
		
		TagID tenthChildOfFourth = thirdChildOfFourth.makeNext().makeNext().makeNext()
										.makeNext().makeNext().makeNext().makeNext();
		Assert.assertNotNull(tenthChildOfFourth);
		Assert.assertEquals("d.j", tenthChildOfFourth.getValue());
		Assert.assertEquals(9, tenthChildOfFourth.intValue());
		
		TagID firstChildOfTenthChildOfFourth = tenthChildOfFourth.makeForFirstChild();
		Assert.assertNotNull(firstChildOfTenthChildOfFourth);
		Assert.assertEquals("d.j.a", firstChildOfTenthChildOfFourth.getValue());
		Assert.assertEquals(0, firstChildOfTenthChildOfFourth.intValue());
		
		TagID last = firstChildOfTenthChildOfFourth;
		for (int i = 0; i < 64; i++) last = last.makeNext();
		Assert.assertNotNull(last);
		Assert.assertEquals("d.j.am", last.getValue());
		Assert.assertEquals(64, last.intValue());
		
		TagID deepChild = last.makeForFirstChild().makeForFirstChild().makeNext().makeForFirstChild().makeForFirstChild().makeNext().makeNext();
		Assert.assertNotNull(deepChild);
		Assert.assertEquals("d.j.am.a.b.a.c", deepChild.getValue());
		Assert.assertEquals(2, deepChild.intValue());
		
		last = deepChild;
		for (int i = 0; i < 357; i++) last = last.makeNext(); // 357 + 2 = (6 * 52) + 47
		Assert.assertNotNull(last);
		Assert.assertEquals("d.j.am.a.b.a.fV", last.getValue());
		Assert.assertEquals(359, last.intValue());		
	}
	
}
