package homeprovider.ca;
import java.lang.reflect.AnnotatedElement;
import javax.lang.model.element.Modifier;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
//import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes(value = ("homeprovider.ca.Encapsulated"))
public class EncapsulatedAnnotationProcessor extends AbstractProcessor{

	private Messager messanger;
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		messanger = processingEnv.getMessager();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		for (TypeElement te: annotations) {
			Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(te);
			for (Element el : annotatedElements) {
				try {			
					
					List<? extends Element> ch = el.getEnclosedElements();
					
					List<? extends Element> fields = new ArrayList<Element>(ch);
					fields.removeIf(e ->e.getKind()!=ElementKind.FIELD);
					
					List<? extends Element> methods = new ArrayList<Element>(ch);
					methods.removeIf(e ->e.getKind()!=ElementKind.METHOD || !e.getModifiers().contains(Modifier.PUBLIC));
						
					for (Element field : fields){
							Set<javax.lang.model.element.Modifier> modifiers = field.getModifiers();
							Modifier modifier = modifiers.stream().findFirst().get();
									//modifiers.stream().filter(f ->f.equals(Modifier.PRIVATE)).findFirst().get();
							
							//anyMatch(f ->f.equals(Modifier.PRIVATE));
							
						    if (!modifier.equals(Modifier.PRIVATE)) {   
						    	messanger.printMessage(Kind.ERROR, "Field is not private", field);
						    }
				            //break;
				            
						    String fieldName = field.getSimpleName().toString();
						    fieldName = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
						    String getterName = "get" + fieldName;
						    String setterName = "set" + fieldName;
				            boolean isGetterFound = false;
				            boolean isSetterFound = false;
						    for (Element method : methods) {
								if(method.getSimpleName().toString().equals(getterName)) {
									isGetterFound = true;
								}
								
								if(method.getSimpleName().toString().equals(setterName)) {
									isSetterFound = true;
								}
							}
						    if (!isGetterFound ) {
						    	messanger.printMessage(Kind.ERROR, "Missed get method ", field);
						    }
						    
						    if (!isSetterFound ) {
						    	messanger.printMessage(Kind.ERROR, "Missed set methods ", field);
						    }
				    }
				} catch (SecurityException e1) {
					e1.printStackTrace();
				}
				
			}
		}
		
		return true;
	}
	}
