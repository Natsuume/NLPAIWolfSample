package org.aiwolf.sample.nl;

/**
 * KNPの形態素クラス
 */
public class Morpheme {
	private String baseInputLine;

	//活用形
	private String conjugatedForm;
	//活用形id
	private int conjugatedFormIdx;
	//活用型
	private String conjugatedType;
	//活用型id
	private int conjugatedTypeIdx;
	//品詞細分類
	private String detailPos;
	//品詞細分類id
	private int detailPosIdx;
	//原型
	private String form;
	//入力形態素
	private String inputText;
	//読み
	private String phonetic;
	//品詞
	private String pos;
	//品詞id
	private int posIdx;

	public Morpheme(String line){
		baseInputLine = line;
		String[] array = line.split(" ");

		//形態素のfeatureは現在使用していないので取得しない
		for(int i = 0; i < 11;i++){
			switch(i){
			case 0: inputText = array[i]; break;
			case 1: phonetic = array[i]; break;
			case 2: form = array[i]; break;
			case 3: pos = array[i]; break;
			case 4: posIdx = Integer.parseInt(array[i]); break;
			case 5: detailPos = array[i]; break;
			case 6: detailPosIdx = Integer.parseInt(array[i]); break;
			case 7: conjugatedType = array[i]; break;
			case 8: conjugatedTypeIdx = Integer.parseInt(array[i]); break;
			case 9: conjugatedForm = array[i]; break;
			case 10: conjugatedFormIdx = Integer.parseInt(array[i]); break;
			}
		}
	}

	public String getBaseInputLine() {
		return baseInputLine;
	}

	public String getConjugatedForm() {
		return conjugatedForm;
	}

	public int getConjugatedFormIdx() {
		return conjugatedFormIdx;
	}

	public String getConjugatedType() {
		return conjugatedType;
	}

	public int getConjugatedTypeIdx() {
		return conjugatedTypeIdx;
	}

	public String getDetailPos() {
		return detailPos;
	}

	public int getDetailPosIdx() {
		return detailPosIdx;
	}

	public String getForm() {
		return form;
	}

	public String getInputText() {
		return inputText;
	}

	public String getPhonetic() {
		return phonetic;
	}

	public String getPos() {
		return pos;
	}

	public int getPosIdx() {
		return posIdx;
	}
}
