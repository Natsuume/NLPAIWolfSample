package org.aiwolf.sample.nl;

import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.Operator;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Talk;
import org.aiwolf.ui.res.JapaneseResource;

public class JapaneseConverter extends JapaneseResource {

	/**
	 * プロトコル文を自然言語に変換する
	 * @param text
	 * @return
	 */
	public String convertTalk(String text){
		if(text == Talk.OVER || text == Talk.SKIP){
			return text;
		}
		Content contents = new Content(text);
		boolean request = contents.getOperator() == Operator.REQUEST;
		Content content = request ? contents.getContentList().get(0) : contents;
		Topic topic = request ? contents.getContentList().get(0).getTopic() : content.getTopic();
		return topicToText(content, topic, request);
	}

	private String topicToText(Content contents,Topic topic, boolean request){
		Agent subject = contents.getSubject();
		Agent target = contents.getTarget();
		String subjectText = subject != null ? "Agent[" + (subject.getAgentIdx() < 10 ? "0" + subject.getAgentIdx() : subject.getAgentIdx()) + "]" : "";
		String targetText = target != null ?  "Agent[" + (target.getAgentIdx() < 10 ? "0" + target.getAgentIdx() : target.getAgentIdx()) + "]"  : "";
		String s1 = request ? "してほしい":"する";
		String s2 = request ? "ってほしい" : "う";
		String s3 = request && subject != null ? subjectText+"に" : "" ;
		switch(topic){
			case ATTACK:
				return String.format("%s%sを襲撃%s",s3,targetText, s1);
			case AGREE:
			case DISAGREE:
				return String.format("%s%d日目の%d番目の発話に%s%s",s3,contents.getTalkDay(), contents.getTalkID(),topic == Topic.AGREE ? "賛成" : "反対",s1);
			case COMINGOUT:
				String trueText = targetText+(request ? "に" : "が");
				String falseText = (subject != null && target != null ? subjectText+"に"+targetText+"が" : "");
				String agentText = subject ==null && target != null ? trueText : falseText;
				return String.format("%s%sだと宣言%s",agentText, convert(contents.getRole()),s1);
			case DIVINED:
				return String.format("%s%sを占った結果%s%s",s3,targetText, convert(contents.getResult()),request ? "だったと言ってほしい":"だった");
			case DIVINATION:
				return String.format("%s%sを占%s",s3, targetText,s2);
			case ESTIMATE:
				return String.format("%s%sが%sだと思%s",s3, targetText, convert(contents.getRole()),s2);
			case GUARDED:
				return String.format("%s%sを護衛%s",s3,targetText, request ? "したと言ってほしい" : "した");
			case GUARD:
				return String.format("%s%sを護衛%s",s3,targetText, s1);
			case IDENTIFIED:
				return String.format("%s霊能結果は%sが%s%s",s3,targetText, convert(contents.getResult()),request ? "だったと言ってほしい":"だった");
			case VOTE:
				return String.format("%s%sに投票%s",s3,targetText, s1);
			default:
				return contents.getText();
		}
	}
}

