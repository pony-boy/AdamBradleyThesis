package annotationInteraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class POSTagger {
	
	private Properties annotator_properties = new Properties();
	private StanfordCoreNLP annotator_pipeline;
	
	public POSTagger(Poem poem_content){
		
		set_annotator_properties_and_pipeline();
		
		List<Line> lines_in_header = poem_content.getPoemHeader().getLines();
		for(int i = 0; i < lines_in_header.size(); i++){
		
			set_line_pos(lines_in_header.get(i));
			
		}
		
		List<Stanza> poem_stanzas = poem_content.getPoemStanzas().getStanzas();
		for(int i = 0; i < poem_stanzas.size(); i++){
			
			List<Line> lines_in_stanza = poem_stanzas.get(i).getLines();
			for(int j = 0; j < lines_in_stanza.size(); j++){
				
				set_line_pos(lines_in_stanza.get(j));
				
			}
			
		}
		
	}
	
	private void set_annotator_properties_and_pipeline(){
		
		//annotator_properties.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		annotator_properties.put("annotators", "tokenize, ssplit, pos");
		annotator_properties.put("ssplit", "ssplit.isOneSentence");
		//annotator_properties.put("ner.model", "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz");
		//annotator_properties.put("ner.applyNumericClassifiers", "false");
		annotator_pipeline = new StanfordCoreNLP(annotator_properties);
		
	}

	private void set_line_pos(Line line_in_stanza){
	
		List<Word> words_in_line = line_in_stanza.getWords();
		Annotation line_annotation = new Annotation(line_in_stanza.getLine());
		annotator_pipeline.annotate(line_annotation);

		int word_index = 0;
		for(CoreMap token : line_annotation.get(CoreAnnotations.TokensAnnotation.class)){
			
			words_in_line.get(word_index).setPOS(token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
			word_index++;
			
		}
	
	}

}
