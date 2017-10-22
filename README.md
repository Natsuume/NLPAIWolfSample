# NLPAIWolfSample

## 概要  
* 本プログラムは「[人狼知能で学ぶAIプログラミング 欺瞞・推理・会話で不完全情報ゲームを戦う人工知能の作り方](https://book.mynavi.jp/supportsite/detail/9784839960582.html)」の「第7章 自然言語処理を利用した人狼知能エージェント」で使用したソースコードです。
* 本プログラムは「自然言語から人狼知能プロトコルへの変換」および「人狼知能プロトコルから自然言語への変換」に関する内容のみを含みます。
* 本プログラムを使用して対戦を行うためには別途エージェントプログラムが必要になります。
* 人狼知能プラットフォームのjarは含めていないため、別途[人狼知能プロジェクト](http://aiwolf.org/ "aiwolf.org")より入手する必要があります。
* 書籍内で省略したKNPの実行・解析結果取得に使用した「KNP」「Phrase」および書籍非掲載の「KnpProcess」「Morhpeme」のクラスを含んでいます。
  
* **プログラムの一部に誤りや想定通りの動作をしていない箇所が見つかったため下記の通りに修正を行っています。**

## 修正点
### リスト7-6 数字・エージェントを表すプロトコルの追加  

* (書籍記載の内容)  
```
protocolList.add(new Protocol(String.format("%d,NUMBER,%d", i)));
protocolList.add(new Protocol(String.format("Agent[%02d],CHARACTER,Agent[%02d]", i)));
```
* (修正後)  
```
protocolList.add(new Protocol(String.format("%d,NUMBER,%d", i, i)));  
protocolList.add(new Protocol(String.format("Agent[%02d],CHARACTER,Agent[%02d]", i, i)));  
```
  
### リスト7-7 入力文のフィルタリング・修正  

* (書籍記載の内容)   
```
textList = (省略) .replaceFirst(">>Agent\\\\[[0-9]{2,}\\\\", "").split("。"));
```
* (修正後) 
```
textList = (省略) .replaceFirst(">>Agent\\\\[[0-9]{2,}\\\\]", "").split("。"));
```
  
### リスト7-10 プロトコルの取得  
  
* (書籍記載の内容)  
```
for(Protocol protocol : allProtocolList.stream()
  .filter(p -> p.getJapanese().equals(word)).collect(Collectors.toList())){  
```
* (修正後) 
```
for(Protocol protocol : allProtocolList.stream()
  .filter(p -> word.contains(p.getJapanese())).collect(Collectors.toList())){  
```
### リスト7-14 プロトコルの並び替え  

* (書籍記載の内容)  
```
ProtocolType[][] sentenceType = {
    {ProtocolType.VERB,ProtocolType.CHARACTER,ProtocolType.ROLE},
    {ProtocolType.VERB,ProtocolType.CHARACTER,ProtocolType.SPECIES},
    {ProtocolType.VERB,ProtocolType.CHARACTER},
    {ProtocolType.VERB,ProtocolType.TALKTYPE,ProtocolType.DAY,ProtocolType.SPEECH}
};
```
```
if(type == ProtocolType.CHARACTER){
  sortedProtocolList.add(agentList.get(0));
}else{
```
* (修正後) 
```
ProtocolType[][] sentenceType = {
    {ProtocolType.VERB,ProtocolType.CHARACTER,ProtocolType.ROLE},
    {ProtocolType.VERB,ProtocolType.CHARACTER,ProtocolType.SPECIES},
    {ProtocolType.VERB,ProtocolType.CHARACTER},
    {ProtocolType.CHARACTER, ProtocolType.VERB,ProtocolType.TALKTYPE,ProtocolType.DAY,ProtocolType.SPEECH}
};
```
```
if(type == ProtocolType.CHARACTER){
  if(agentList.isEmpty()) continue;
  sortedProtocolList.add(agentList.get(0));
}else{
```
