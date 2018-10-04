package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	//템플릿&콜백 적용
	public Integer calcSum(String filepath) throws IOException {
		//콜백(변하는부분) 불러와서 어떤작업을 할지 코딩함.
		LineCallback<Integer> sumCallBack = 
			new LineCallback<Integer>() {
				public Integer doSomethingWithLine(String line, Integer value) {
					return value + Integer.valueOf(line);
				}
		};
		return lineReadTemplate(filepath, sumCallBack, 0);
	}
	
	public Integer calcMultiply(String filepath) throws IOException {
		LineCallback<Integer> mulCallback = 
			new LineCallback<Integer>() {
				public Integer doSomethingWithLine (String line, Integer value) {
					return value * Integer.valueOf(line);
				}
			};
		return lineReadTemplate(filepath, mulCallback, 1);
	}
	
	public Integer fileReadTemplate(String filepath, BufferedReaderCallBack callback)
		throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			int ret = callback.doSomethingWithReader(br);
			return ret;
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		finally {
			if (br != null) {
				try {br.close();}
				catch(IOException e) { System.out.println(e.getMessage()); }
			}
		}
	}
	
	public <T> T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal)
		throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			T res = initVal;
			String line = null;
			while((line = br.readLine()) != null) {
				res = callback.doSomethingWithLine(line, res);
			}
			return res;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			if(br != null) {
				try {br.close();}
				catch (IOException e) {System.out.println(e.getMessage());}
			}
		}
	}
	
	public String concatenate(String filepath) throws IOException {
		LineCallback<String> concatenateCallback = 
			new LineCallback<String>() {
			public String doSomethingWithLine(String line, String value) {
				return value + line;
			}
		};
		return lineReadTemplate(filepath, concatenateCallback, "");
	}
}
