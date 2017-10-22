package org.aiwolf.sample.nl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * KNPの実行、結果の読み込みの窓口クラス
 */
public class KNP {
	//knpのプロセスを管理するインスタンス
	private KnpProcess knpProcess;

	//結果の格納
	Map<String, List<Phrase>> resultMap = new HashMap<>();


	public KNP(String juman, String knp){
		//windowsの場合はshift-jis,それ以外はutf-8にする
		this(juman, knp, System.getProperty("os.name").toLowerCase().startsWith("windows"));
	}

	public KNP(String juman, String knp, boolean isWindows){
		String encode = isWindows ? "shift-jis" : "utf-8";
		String[] command = {
				(isWindows ? "cmd.exe" : "/bin/sh"),
				(isWindows ? "/c" : "-c"),
				juman,
				"|",
				knp,
				"-tab"
		};

		//プロセスの実行
		ProcessBuilder pb = new ProcessBuilder(command);
		try {
			knpProcess = new KnpProcess(pb.start(), encode);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 引数に与えた自然言語文をKNPで解析し、その結果をList<Phrase>として返す
	 * @param text
	 * @return
	 */
	public List<Phrase> getKnpResult(String text){
		List<Phrase> phraseList = new ArrayList<>();
		List<String> list = knpProcess.getKnpResult(text);

		//確認のため結果をコンソールに出力
		//list.forEach(System.out::println);

		//KNPの出力から利用する情報を抽出
		List<String> phraseLines = new ArrayList<>();
		int idx = 0;
		for(String line : list){
			if(line.startsWith("*") || line.startsWith("#") || line.equals("EOS")){
				if(phraseLines.isEmpty()) continue;
				phraseList.add(new Phrase(phraseLines, idx++));
				phraseLines = new ArrayList<>();
			}else{
				if(line.startsWith("+") && !phraseLines.isEmpty()){
					phraseList.add(new Phrase(phraseLines, idx++));
					phraseLines = new ArrayList<>();
				}
				phraseLines.add(line);
			}
		}

		return phraseList;
	}

	/**
	 * プロセスを明示的に終了する
	 */
	public void close(){
		knpProcess.close();
	}
}