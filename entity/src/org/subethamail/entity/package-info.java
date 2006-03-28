/*
 * $Id: Person.java 125 2006-03-07 13:27:43Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/entity/src/com/blorn/entity/Person.java $
 */

@org.hibernate.annotations.TypeDefs({
	@org.hibernate.annotations.TypeDef(
		name="anyImmutable",
		typeClass=org.subethamail.entity.type.AnyImmutableType.class            
	)
})

package org.subethamail.entity;

