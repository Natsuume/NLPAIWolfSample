package org.aiwolf.sample.nl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * KNPの句に対応するクラス
 */
public class Phrase {


	private static final Pattern FEATURE_PATTERN = Pattern.compile("(?<=<).+?(?=>)");

	private List<String> features = new ArrayList<>();
	//入力
	private String inputText = "";
	//形態素リスト
	private List<Morpheme> morphemeList = new ArrayList<>();
	//id
	private int phraseIdx;
	//係り先のPhraseのid
	private int targetIdx;
	//係り受けタイプ
	private String targetType;

	public Phrase(List<String> list, int idx){
		this.phraseIdx = idx;
		for(int i =0; i < list.size(); i++){
			if(i == 0){
				String[] array = list.get(i).split(" ");
				targetIdx = Integer.parseInt(array[1].substring(0, array[1].length()-1));
				targetType = array[1].substring(array[1].length()-1);
				Matcher matcher = FEATURE_PATTERN.matcher(array[2]);
				while(matcher.find()){
					features.add(matcher.group());
				}
			}else{
				Morpheme morpheme = new Morpheme(list.get(i));
				morphemeList.add(morpheme);
				inputText+=morpheme.getInputText();
			}
		}
	}

	public List<String> getFeatures() {
		// TODO 自動生成されたメソッド・スタブ
		return features;
	}

	public String getInputText() {
		// TODO 自動生成されたメソッド・スタブ
		return inputText;
	}

	public List<Morpheme> getMorphemeList() {
		return morphemeList;
	}

	public int getPhraseIdx() {
		return phraseIdx;
	}

	public int getTargetIdx() {
		return targetIdx;
	}

	public String getTargetType() {
		return targetType;
	}
}

