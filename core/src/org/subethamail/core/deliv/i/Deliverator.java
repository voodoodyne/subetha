/*
 * $Id: AccountMgr.java 127 2006-03-15 02:29:11Z jeff $
 * $URL: https://svn.infohazard.org/blorn/trunk/core/src/com/blorn/core/acct/i/AccountMgr.java $
 */

package org.subethamail.core.deliv.i;

import javax.ejb.Local;

/**
 * Interface for delivering customized mail to a real individuals.
 *

The Deliverator belongs to an elite order, a hallowed subcategory. He's
got esprit up to here. Right now, he is preparing to carry out his third
mission of the night. His uniform is black as activated charcoal,
filtering the very light out of the air. A bullet will bounce off its
arachnofiber weave like a wren hitting a patio door, but excess
perspiration wafts through it like a breeze through a freshly napalmed
forest. Where his body has bony extremities, the suit has sintered
armorgel: feels like gritty jello, protects like a stack of telephone
books.

When they gave him the job, they gave him a gun. The deliverator never
deals in cash, but someone might come after him anyway---might want his
car, or his cargo. The gun is tiny, aero-styled, lightweight, the kind
of gun a fashion designer would carry; it fires teensy darts that fly at
five times the velocity of an SR-71 spy plane, and when you get done
using it, you have to plug it into the cigarette lighter, because it
runs on electricity.

The Deliverator never pulled that gun in anger, or in fear. He pulled it
once in Gila Highlands. Some punks in Gila Highlands, a fancy burbclave,
wanted themselves a delivery, but they didn't want to pay for it.
Thought they would impress the Deliverator with a baseball bat. The
Deliverator took out his gun, centered its laser doohickey on that
poised Louisville Slugger, fired it. The recoil was immense, as though
the waepon had blown up in his hand. The middle third of the baseball
bat turned into a column of burning sawdust accelerating in all
directions like a bursting star. Punk ended up holding this bat handle
with a milky smoke pouring out the end. Stupid look on his face. Didn't
get nothing but trouble from the Deliverator.

Since then the Deliverator has kept the gun in the glove compartment and
relied, instead, on a matched set of samurai swords, which had always
been his weapon of choice anyhow. The punks in Gila Highland weren't
afraid of the gun, so the Deliverator was forced to use it. But swords
need no demonstrations.

The deliverators car has enough potential energy packed into its
batteries to fire a pound of bacon into the Asteroid Belt. Unlike a
bimbo box or a Burb beater, the Deliverators car unloads that power
through gaping gleaming, polished sphincters. When the deliverator puts
the hammer down, shit happens. You want to talk contact patches? Your
car's tires have tiny contact patches, talk to the asphalt in four
places the size of your tongue. The Deliverator's car has big sticky
tires with contact patches the size of a fat lady's thighs. The
Deliverator is in touch with the road, starts like a bad day, stops on a
peseta.

Why is the Deliverator so equipped? Because people rely on him. He is a
roll model. This is America. People do whatever the fuck they feel like
doing, you got a problem with that? Because they have a right to. And
because they have guns and noone can fucking stop them. As a result,
this country has one of the worst economies in the world. When it gets
down to it---talking trade balances here---once we've brain-drained all
our technology into other countries, once things have evened out,
they're making cars in Bolivia and microwave ovens in Tadzhikistan and
selling them here---once our edge in natural resources has been made
irrelevant by giant Hong Kong ships and dirigibles that can ship North
Dakota all the way to New Zealand for a nickel---once the Invisible Hand
has taken all those historical inequities and smeared them out into a
broad global layer of what a Pakistani brickmaker would consider to be
prosperity---y'know waht? There's only four things we do better than
anyone else

	music
	movies
	microcode (software)
	high-speed pizza delivery

The Deliverator used to make software. Still does, sometimes. But if
life were run by well-meaning education Ph.D.s, the Deliverator's report
card would say: ``Hiro is so bright and creative but needs to work
harder on his cooperation skills.''

So now he has this other job. No brightness or creativity involved---but
no cooperation either. Just a single principle: The Deliverator stands
tall, your pie in thirty minutes or you can have it free, shoot the
driver, take his car, file a class-action suit. The Deliverator has been
working this job for six months, a rich and lengthy tenure by his
standards, and has never delivered a pizza in more than twenty-one
minutes.

 *
 * @author Jeff Schnitzer
 */
@Local
public interface Deliverator
{
	/** */
	public static final String JNDI_NAME = "Deliverator/local";

	/**
	 * Actually delivers a piece of mail to a person.  No queueing
	 * involved.  The mail will be customized for the person.
	 */
	public void deliver(Long mailId, Long personId);
}

