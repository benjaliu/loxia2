package loxia.sample;

import java.util.ArrayList;
import java.util.List;

import loxia.support.json.JSONArray;

import org.testng.annotations.Test;

@Test
public class JsonTest {

	@Test
	public void testWrite() throws Exception{
		List<Menu> menus = new ArrayList<Menu>();
		for(int i=0; i< 10; i++){
			Menu m = new Menu();
			m.setName(""+i);
			for(int j=0; j<10; j++){
				Menu mm = new Menu();
				mm.setName(i+"-"+j);
				for(int k=0; k<10; k++){
					Menu mmm = new Menu();
					mmm.setName(i+"-"+j + "-" + k);
					mm.getChildren().add(mmm);
				}
				m.getChildren().add(mm);
			}
			menus.add(m);
		}
		System.out.println(new JSONArray(menus,"***"));
	}
}
