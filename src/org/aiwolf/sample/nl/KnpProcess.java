package org.aiwolf.sample.nl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class KnpProcess{
	private Process process;
	private String encode;

	private Thread errorThread;
	private BufferedWriter bufferedWriter;
	private ExecutorService executor;

	public KnpProcess(Process process, String encode){
		this.process = process;
		this.encode = encode;
		this.executor = Executors.newSingleThreadExecutor();

		this.errorThread = new Thread(new StreamReader(process.getErrorStream(), encode));
		try {
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), encode));
		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		errorThread.start();

	}

	/**
	 * textをKNPで解析し、その結果をList<String>で返す
	 * @param text
	 * @return
	 */
	public List<String> getKnpResult(String text){
		List<String> resultList = new ArrayList<>();

		try {
			sendMessage(text);

			Future<List<String>> future = executor.submit(new KnpResultCallable(process.getInputStream(), encode));
			resultList = future.get();
		} catch (InterruptedException | ExecutionException | IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return resultList;
	}

	/**
	 * プロセスを明示的に終了する
	 */
	public void close(){
		this.process.destroy();
	}

	/**
	 * KNPへのテキストの送信
	 * @param text
	 * @throws IOException
	 */
	private void sendMessage(String text) throws IOException{
		bufferedWriter.write(text);
		bufferedWriter.newLine();
		bufferedWriter.flush();
	}
}

/**
 * KNPのInputStream取得クラス
 */
class KnpResultCallable implements Callable<List<String>>{
	private BufferedReader bufferedReader;

	public KnpResultCallable(InputStream stream, String encode){
		try {
			this.bufferedReader = new BufferedReader(new InputStreamReader(stream, encode));
		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public List<String> call(){
		List<String> resultList = new ArrayList<>();

		try{
			while(true){
				String line = bufferedReader.readLine();
				resultList.add(line);
				if(line.equals("EOS")) break;
			}
		}catch(IOException e){
			e.printStackTrace();
		}

		return resultList;
	}
}

/**
 * エラーストリーム取得クラス
 */
class StreamReader implements Runnable{
	private InputStream stream;
	private String encode;


	StreamReader(InputStream stream, String encode){
		this.stream = stream;
		this.encode = encode;
	}

	@Override
	public void run() {
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, encode));
			String line;
			while((line = br.readLine()) != null){
				System.err.println(line);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
