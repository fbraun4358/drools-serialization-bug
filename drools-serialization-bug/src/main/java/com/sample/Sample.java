package com.sample;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;

public class Sample {
	
	public static final String DRL_FILE = "sample.drl";
	public static final String RF_FILE = "sample.rf";
	
	private static KieBase loadKieBase() {
		KieServices services = KieServices.Factory.get();
		KieFileSystem kfs = services.newKieFileSystem();
		
		Resource drl = services.getResources()
				.newFileSystemResource("src/main/resources/"+DRL_FILE);
		Resource rf = services.getResources()
				.newFileSystemResource("src/main/resources/"+RF_FILE);
		
		kfs.write(drl);
		kfs.write(rf);

		KieBuilder kieBuilder = services.newKieBuilder(kfs).buildAll();
		
		if (kieBuilder.getResults().hasMessages(Level.ERROR)) {
			for(Message m : kieBuilder.getResults().getMessages(Level.ERROR)){
				throw new RuntimeException(m.getText());
			}
		}
		
		KieContainer container = services.newKieContainer(
				services.getRepository().getDefaultReleaseId());
		
		return container.getKieBase();
	}
	
	private static ByteArrayOutputStream serializeKieBase(KieBase kieBase) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
	    oos.writeObject(kieBase);
	    oos.close();
	    
		return bos;
	}
	
	private static KieBase deserializeKieBase(ByteArrayInputStream bis) throws Exception {
		ObjectInputStream ois = new ObjectInputStream( bis );
		KieBase kieBase = (KieBase)ois.readObject();
		ois.close();
		
		return kieBase;
	}
	
	public static void main(String[] args) throws Exception {
		KieBase kieBase = loadKieBase();
		
		ByteArrayOutputStream bos = serializeKieBase(kieBase);
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		
		deserializeKieBase(bis);
		
		System.out.println("Success!");
	}
}
