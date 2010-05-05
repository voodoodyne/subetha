package org.subethamail.core.util;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 *
 */
public class InjectBeanHelper<T>
{

	@Inject
	BeanManager mgr = null;

	/** */
	@SuppressWarnings("unchecked")
	public T getInstance(String clazz) throws ClassNotFoundException
	{
		Class<? extends T> tc = (Class<? extends T>) Class.forName(clazz);
		return getInstance(tc);
	}

	/** */
	@SuppressWarnings("unchecked")
	public T getInstance(Class<? extends T> tc)
	{
		Set<Bean<?>> tcBeans = this.mgr.getBeans(tc, new AnnotationLiteral<Any>(){});
		
		if (tcBeans == null || tcBeans.size() == 0) throw new RuntimeException("No matching beans for class=" + tc.getName());
		if (tcBeans.size() > 1) throw new RuntimeException("Too many matching beans for class=" + tc.getName());
		Bean<?> tcBean = tcBeans.iterator().next();
		CreationalContext<?> cc = this.mgr.createCreationalContext(tcBean);
		return (T) this.mgr.getReference(tcBean, tc, cc);
	}
}
