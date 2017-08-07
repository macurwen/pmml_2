package com.redhat.camel.route.pmml;

import com.google.gson.Gson;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.*;
import org.jpmml.model.PMMLUtil;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PmmlProcessor implements Processor{

   public void process(Exchange exhange) {
       PMML pmml = null;

       File file = new File("/home/mcurwen/dev/src/projects/java/1.8/redhat/pmml/src/test/pmml/pmml/RegressionAuto.pmml");

       try (InputStream is = new FileInputStream(file)) {
           pmml = PMMLUtil.unmarshal(is);

       } catch (FileNotFoundException fnf) {
           fnf.printStackTrace();
       } catch (IOException ioe) {
           ioe.printStackTrace();
       } catch (SAXException sae) {
           sae.printStackTrace();
       } catch (JAXBException jae) {
           jae.printStackTrace();
       }

       if (null != pmml) {
           ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
           ModelEvaluator<?> modelEvaluator = modelEvaluatorFactory.newModelEvaluator(pmml);

           Evaluator evaluator = (Evaluator)modelEvaluator;



           List<List<String>> data = (List<List<String>>) exhange.getIn().getBody();
           Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
           List<InputField> inputFields = evaluator.getInputFields();
           data.forEach(line->{
                   for (int x=0;x< inputFields.size();x++) {
                       InputField inputField = inputFields.get(x);
                       FieldName inputFieldName = inputField.getName();

                       Object rawValue = line.get(x);
                       FieldValue inputFieldValue = inputField.prepare(rawValue);

                       arguments.put(inputFieldName, inputFieldValue);
                   }
           });
           Gson gson = new Gson();
           String json = gson.toJson(evaluator.evaluate(arguments));

           exhange.getIn().setBody(json);
       }

   }
}
