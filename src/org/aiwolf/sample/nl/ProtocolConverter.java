package org.aiwolf.sample.nl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;

public class ProtocolConverter {
	private final String path = "res/protocol.csv";
	private List<Protocol> protocolList = new ArrayList<>();
	private final int max = 99;

	public List<Protocol> getProtocolList(){
		return protocolList;
	}

	/**
	 * テスト実行用メソッド
	 * @param args
	 */
	public static void main(String[] args){
		//自然言語->人狼知能プロトコル変換テスト
		try {
			List<String> inputTextList = Files.readAllLines(Paths.get("res", "TestData.txt"));
			List<String> convertedProtocolTextList = new ArrayList<>();
			List<String> reconvertedJapaneseTextList = new ArrayList<>();
			ProtocolConverter protocolConverter = new ProtocolConverter();
			JapaneseConverter japaneseConverter = new JapaneseConverter();
			for(String text : inputTextList){
				String protocolText =protocolConverter.convert(new Talk(0,0,0,Agent.getAgent(0),text));
				String japaneseText = japaneseConverter.convertTalk(protocolText);
				convertedProtocolTextList.add(protocolText);
				//printContent(new Content(protocolText));
				reconvertedJapaneseTextList.add(japaneseText);
				if(!text.equals(japaneseText)) System.err.println("reconvert failed. : " + text + " -> " + japaneseText);
			}

			for(int i = 0; i < inputTextList.size(); i++){
				System.out.println(inputTextList.get(i)  + " -> " + convertedProtocolTextList.get(i) + " -> " + reconvertedJapaneseTextList.get(i));
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		/*
		ProtocolConverter converter = new ProtocolConverter();
		converter.getProtocolList().stream().forEach(System.out::println);
		Talk talk = new Talk(0, 0, 0, Agent.getAgent(0), "Agent[01]を占った結果人狼だった");
		String protocol = converter.convert(talk);
		Content content = new Content(protocol);
		System.out.println("Input : " + talk.getText());
		System.out.println("Protocol : " + content.getText());
		System.out.println("Japanese : " + new JapaneseConverter().convertTalk(content.getText()));
		System.out.println("TestPrint");
		printContent(content);
		*/
		System.exit(0);
	}

	/**
	 * 変換したプロトコル文がContentで認識されているか確認する
	 * @param content
	 */
	private static void printContent(Content content){
		System.out.println("Text : " + content.getText());
		System.out.println("Subject : " + content.getSubject());
		Topic topic = content.getTopic();
		if(topic == Topic.AGREE || topic == Topic.DISAGREE){
			System.out.println("TalkType : " + content.getTalkType());
			System.out.println("TalkDay : " + content.getTalkDay());
			System.out.println("TalkID : " + content.getTalkID());
		}else if(topic == Topic.OPERATOR){
			content.getContentList().stream().forEach(c -> printContent(c));
		}else{
			if(topic == Topic.ESTIMATE || topic == Topic.COMINGOUT){
				System.out.println("Role : " + content.getRole());
			}else if(topic == Topic.DIVINED || topic == Topic.IDENTIFIED){
				System.out.println("Resut : " + content.getResult());
			}
			System.out.println("Target : " + content.getTarget());
		}
	}

	public ProtocolConverter(){
		initialize(path);
	}

	public ProtocolConverter(String path){
		initialize(path);
	}

	/**
	 * 初期化
	 * @param path
	 */
	private void initialize(String path){
		//プロトコル情報を記述したCSVファイルの読み込み
		InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path));
		BufferedReader br = new BufferedReader(reader);
		try {
			String line = br.readLine();
			while(line != null){
				protocolList.add(new Protocol(line));
				line = br.readLine();
			}
		}catch(IOException e){
			e.printStackTrace();
		}

		//数字・エージェント名を表すプロトコルの登録
		for(int i = 0 ; i <= max ; i++){
			protocolList.add(new Protocol(String.format("%d,NUMBER,%d", i, i)));
			protocolList.add(new Protocol(String.format("Agent[%02d],CHARACTER,Agent[%02d]", i, i)));
		}
	}

	/**
	 * 引数にtalkを取り、発話内容の自然言語をプロトコルに変換し、変換後のプロトコル文字列をStringで返す
	 * @param talk
	 * @return
	 */
	public String convert(Talk talk){
		String resultText = "";
		List<String> textList = new ArrayList<>();
		List<String>  protocolTextList = new ArrayList<>();

		//アンカーや記号の削除など文章の整形、発話内容を文毎に区切りListに格納する
		textList = Arrays.asList(
				talk.getText().replaceAll("[ 　]", "")
				.replaceAll("[！？]", "。")
				.replaceFirst(">>Agent\\[[0-9]{2,}\\]", "")
				.split("。")
		);

		//プロトコルへ変換
		protocolTextList = textList.stream().map(text ->{
			//取得したプロトコルを格納するList
			List<Protocol> protocolList = new ArrayList<>();

			//KNPの解析結果の取得
			KNP knp = new KNP("juman","knp");
			List<Phrase> phraseList = knp.getKnpResult(text);
			knp.close();

			//プロトコルの取得
			for(Phrase phrase : phraseList){
				//featureから必要な情報を取得
				String verb = null;
				boolean isRequest = false;
				boolean isPast = false;
				for(String feature : phrase.getFeatures()){
					if(feature.startsWith("用言代表表記")) verb = feature.split(":")[1].split("/")[0];
					if(!isRequest) isRequest = feature.equals("態:ほしい");
					if(!isPast) isPast = feature.equals("時制-過去");
				}

				//プロトコルの日本語文字列と比較してプロトコルを取得
				String word = verb == null ? phrase.getInputText() : verb;
				List<Protocol> allProtocolList = this.protocolList;

				if(isRequest){
					protocolList.add(allProtocolList.stream().filter(p -> p.getProtocol().equals("REQUEST")).findFirst().get());
				}
				for(Protocol protocol : allProtocolList.stream().filter(p -> word.contains(p.getJapanese())).collect(Collectors.toList())){
					String protocolText = protocol.getJapanese();
					if(protocolText.equals("占う")){
						Protocol divined = allProtocolList.stream().filter(p -> p.getProtocol().equals("DIVINED")).findFirst().get();
						protocolList.add(isPast ? divined : protocol);
					}else if(protocolText.equals("護衛") || protocolText.equals("護衛した")){
						Protocol guarded = allProtocolList.stream().filter(p -> p.getProtocol().equals("GUARDED")).findFirst().get();
						protocolList.add(isPast ? guarded : protocol);
					}else if(protocol.getType() == ProtocolType.NUMBER){
						if(!word.startsWith("Agent")){
							protocolList.add(protocol);
						}
					}else{
						protocolList.add(protocol);
					}
				}
			}




			//並び替え
			//dayNumber,TalkNumberの結合
			for(int i = 0; i < protocolList.size(); i++){
				Protocol number = protocolList.get(i);
				if(number.getType() == ProtocolType.NUMBER){
					if(i < protocolList.size()-1){
						Protocol next = protocolList.get(i+1);
						if(next.getType() == ProtocolType.DAY || next.getType() == ProtocolType.SPEECH){
							String numText = number.getJapanese() + next.getJapanese();
							protocolList.remove(number);
							protocolList.remove(next);
							protocolList.add(i, new Protocol(numText + "," + next.getType() + "," + next.getProtocol() + number.getProtocol()));
						}
					}
				}
			}

			//protocolListを人狼知能プロトコルの文法に合うように並び替える
			List<Protocol> sortedProtocolList = new ArrayList<>();
			List<Protocol> agentList	 = new ArrayList<>();
			Map<ProtocolType, Protocol> protocolMap = new HashMap<>();
			ProtocolType[][] sentenceType = {
					{ProtocolType.VERB,ProtocolType.CHARACTER,ProtocolType.ROLE},
					{ProtocolType.VERB,ProtocolType.CHARACTER,ProtocolType.SPECIES},
					{ProtocolType.VERB,ProtocolType.CHARACTER},
					{ProtocolType.CHARACTER, ProtocolType.VERB,ProtocolType.TALKTYPE,ProtocolType.DAY,ProtocolType.SPEECH}
			};

			String[][] verbPattern = {
					{"ESTIMATE","COMINGOUT"},
					{"DIVINED","IDENTIFIED"},
					{"DIVINATION","GUARD","GUARDED","VOTE","ATTACK"},
					{"AGREE","DISAGREE"}
			};
			int patternNum = -1;

			//ProtocolTypeを元に並び替えるためにProtocolTypeをkeyにマップを作成
			for(Protocol protocol : protocolList){
				if(protocol.getType() == ProtocolType.CHARACTER){
					agentList.add(protocol);
				}else{
					protocolMap.put(protocol.getType(), protocol);
				}
			}
			Protocol verb = protocolMap.get(ProtocolType.VERB);
			Protocol request = protocolMap.get(ProtocolType.OPERATOR);

			//プロトコル文のパターン特定とverb・characterが不足していないかの判定
			for(int i = 0; i < verbPattern.length; i++){
				for(int j = 0; j < verbPattern[i].length; j++){
					if(verbPattern[i][j].equals(verb.getProtocol())){
						patternNum = i;
					}
				}
			}
			boolean isIncorrect = (verb == null || patternNum < 0 || (patternNum != 3 && agentList.size() == 0));

			//protocolMapから並び替え
			if(!isIncorrect){
				switch(agentList.size()){
				case 3:
					sortedProtocolList.add(agentList.remove(0));
				case 2:
					if(request != null){
						sortedProtocolList.add(request);
					}
					sortedProtocolList.add(agentList.remove(0));
				default:
					if(!(request == null || sortedProtocolList.contains(request))){
						sortedProtocolList.add(request);
					}
					for(int i = 0; i < sentenceType[patternNum].length; i++){
						ProtocolType type = sentenceType[patternNum][i];
						if(type == ProtocolType.CHARACTER){
							if(agentList.isEmpty()) continue;
							sortedProtocolList.add(agentList.get(0));
						}else{
							Protocol protocol = protocolMap.get(type);
							isIncorrect = protocol == null;
							if(isIncorrect){
								break;
							}
							sortedProtocolList.add(protocol);
						}
					}
				}
			}
			protocolList = sortedProtocolList;



			//protocolListを文字列へ変換
			String protocolString = "";
			if(isIncorrect){
				protocolString = Talk.OVER;
			}else{
				for(int i = 0; i < protocolList.size(); i++){
					Protocol protocol = protocolList.get(i);
					protocolString += protocol.getProtocol();
					if(protocol.getProtocol().equals("REQUEST")){
						protocolString += "(";
					}else if(i < protocolList.size() -1){
						protocolString += " ";
					}
				}
				if(request != null){
					protocolString += ")";
				}
			}

			return protocolString;
		}).collect(Collectors.toList());

		//人狼知能プロトコルは1発話につき1文しか発話できないため、発話文が複数ならどれか一つを選択
		switch(protocolTextList.size()){
		case 0:
			return Talk.OVER;
		case 1:
			return protocolTextList.get(0);
		default:
			return protocolTextList.get(new Random().nextInt(protocolTextList.size()));
		}
	}
}

