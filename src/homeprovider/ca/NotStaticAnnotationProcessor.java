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
import javax.net.ssl.ExtendedSSLSession;
import javax.tools.Diagnostic.Kind;

//@SupportedAnnotationTypes(value = {"homeprovider.ca.NotStatic" , "homeprovider.ca.NoStaticFields"})
@SupportedAnnotationTypes(value = {"homeprovider.ca.NotStatic*"}) //all annotations from homeprovider.ca package with names started with 'NotStatic'
public class NotStaticAnnotationProcessor extends AbstractProcessor{

	private Messager messager;
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		
		messager = processingEnv.getMessager();
		
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		Set<? extends Element> elementsNoStaticFieldsAnnotaion = roundEnv.getElementsAnnotatedWith(homeprovider.ca.NoStaticFields.class);
		for (Element elementF : elementsNoStaticFieldsAnnotaion) {
			List<? extends Element> fields = findStaticFields(elementF);
			
			for (Element field : fields) {	
				boolean isStatic = field.getModifiers().stream().anyMatch(mod -> mod.equals(Modifier.STATIC));
				if(isStatic) {
					messager.printMessage(Kind.ERROR, "Class " + elementF + " does not correspond!", elementF);
					messager.printMessage(Kind.ERROR, "Field can not be static!", field);
					//break;
				}
			}
		}
		
		Set<? extends Element> elementsNotStaticAnnotation = roundEnv.getElementsAnnotatedWith(homeprovider.ca.NotStatic.class);
		
		for (Element element : elementsNotStaticAnnotation) {

			List<? extends Element> chElements =  element.getEnclosedElements();
			
			for (Element chElement : chElements) {
				if(chElement.getModifiers().contains(Modifier.STATIC)) {
					messager.printMessage(Kind.ERROR, "Class " + element + " does not correspond!", element);
					messager.printMessage(Kind.ERROR, "Can not be static!", chElement);
					//break;
				}
			}
			
		}
		return true;
	}
	
	public List<? extends Element> findStaticFields(Element element) {
		List<? extends Element> ch = element.getEnclosedElements();
		
		List<? extends Element> fields = new ArrayList<Element>(ch);
		fields.removeIf(e ->e.getKind()!=ElementKind.FIELD);
		
		return fields;
	}
	
}
