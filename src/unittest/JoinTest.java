package unittest;

import static org.junit.Assert.*;


import org.junit.Test;

import constantpropagation.Content;
import constantpropagation.Join;

public class JoinTest {

	
	@Test
	public void testIntersectTopEntry() {
		Join join = new Join();
		Content contentTop = new Content(Content.TOP);
		Content contentNumber1 = new Content(Content.NUMBER);
		contentNumber1.setNumber(1);
		Content contentNumber2 = new Content(Content.NUMBER);
		contentNumber2.setNumber(2);
		Content contentBottom = new Content(Content.BOTTOM);
		
		Content result = join.intersect(contentTop, contentTop);
		assertEquals(result.getType(),Content.TOP);
		
		result = join.intersect(contentTop, contentBottom);
		assertEquals(result.getType(),Content.BOTTOM);
		
		result = join.intersect(contentTop, contentNumber1);
		assertEquals(result.getType(),Content.NUMBER);
		assertEquals(result.getNumber(),contentNumber1.getNumber());
		
	}
	
	@Test
	public void testIntersectBottomEntry() {
		Join join = new Join();
		Content contentTop = new Content(Content.TOP);
		Content contentNumber1 = new Content(Content.NUMBER);
		contentNumber1.setNumber(1);
		Content contentNumber2 = new Content(Content.NUMBER);
		contentNumber2.setNumber(2);
		Content contentBottom = new Content(Content.BOTTOM);
		
		Content result = join.intersect(contentBottom, contentBottom);
		assertEquals(result.getType(),Content.BOTTOM);
		
		result = join.intersect(contentBottom, contentTop);
		assertEquals(result.getType(),Content.BOTTOM);
		
		result = join.intersect(contentBottom, contentNumber1);
		assertEquals(result.getType(),Content.BOTTOM);
		assertFalse(result.getNumber() == contentNumber1.getNumber());
		
	}
	
	@Test
	public void testIntersectNumberEntry() {
		Join join = new Join();
		Content contentTop = new Content(Content.TOP);
		Content contentNumber1 = new Content(Content.NUMBER);
		contentNumber1.setNumber(1);
		Content contentNumber2 = new Content(Content.NUMBER);
		contentNumber2.setNumber(2);
		Content contentBottom = new Content(Content.BOTTOM);
		
		Content result = join.intersect(contentNumber1, contentBottom);
		assertEquals(result.getType(),Content.BOTTOM);
		
		result = join.intersect(contentNumber1, contentTop);
		assertEquals(result.getType(),Content.NUMBER);
		
		result = join.intersect(contentNumber1, contentNumber2);
		assertEquals(result.getType(),Content.BOTTOM);
		assertFalse(result.getNumber() == contentNumber1.getNumber());
		assertFalse(result.getNumber() == contentNumber2.getNumber());
		
		result = join.intersect(contentNumber1, contentNumber1);
		assertEquals(result.getType(),Content.NUMBER);
		assertFalse(result.getNumber() != contentNumber1.getNumber());
		
	}
	
	//-------------- UNION TESTS ------------------------------------------------
	
	@Test
	public void testUnionTopEntry() {
		Join join = new Join();
		Content contentTop = new Content(Content.TOP);
		Content contentNumber1 = new Content(Content.NUMBER);
		contentNumber1.setNumber(1);
		Content contentNumber2 = new Content(Content.NUMBER);
		contentNumber2.setNumber(2);
		Content contentBottom = new Content(Content.BOTTOM);
		
		Content result = join.union(contentTop, contentTop);
		assertEquals(result.getType(),Content.TOP);
		
		result = join.union(contentTop, contentBottom);
		assertEquals(result.getType(),Content.TOP);
		
		result = join.union(contentTop, contentNumber1);
		assertEquals(result.getType(),Content.TOP);
		assertFalse(result.getNumber()==contentNumber1.getNumber());
		
	}
	
	@Test
	public void testUnionBottomEntry() {
		Join join = new Join();
		Content contentTop = new Content(Content.TOP);
		Content contentNumber1 = new Content(Content.NUMBER);
		contentNumber1.setNumber(1);
		Content contentNumber2 = new Content(Content.NUMBER);
		contentNumber2.setNumber(2);
		Content contentBottom = new Content(Content.BOTTOM);
		
		Content result = join.union(contentBottom, contentBottom);
		assertEquals(result.getType(),Content.BOTTOM);
		
		result = join.union(contentBottom, contentTop);
		assertEquals(result.getType(),Content.TOP);
		
		result = join.union(contentBottom, contentNumber1);
		assertEquals(result.getType(),Content.NUMBER);
		assertFalse(result.getNumber() != contentNumber1.getNumber());
		
	}
	
	@Test
	public void testUnionNumberEntry() {
		Join join = new Join();
		Content contentTop = new Content(Content.TOP);
		Content contentNumber1 = new Content(Content.NUMBER);
		contentNumber1.setNumber(1);
		Content contentNumber2 = new Content(Content.NUMBER);
		contentNumber2.setNumber(2);
		Content contentBottom = new Content(Content.BOTTOM);
		
		Content result = join.union(contentNumber1, contentBottom);
		assertEquals(result.getType(),Content.NUMBER);
		assertEquals(result.getNumber(), contentNumber1.getNumber());
		
		result = join.union(contentNumber1, contentTop);
		assertEquals(result.getType(),Content.TOP);
		
		result = join.union(contentNumber1, contentNumber2);
		assertEquals(result.getType(),Content.TOP);
		assertFalse(result.getNumber() == contentNumber1.getNumber());
		assertFalse(result.getNumber() == contentNumber2.getNumber());
		
		result = join.union(contentNumber1, contentNumber1);
		assertEquals(result.getType(),Content.NUMBER);
		assertFalse(result.getNumber() != contentNumber1.getNumber());
		
	}

}
