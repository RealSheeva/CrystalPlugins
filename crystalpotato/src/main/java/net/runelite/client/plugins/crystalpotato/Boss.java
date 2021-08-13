/*
 * Copyright (c) 2018, Raqes <j.raqes@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.crystalpotato;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.ToString;
import net.runelite.api.NpcID;

import java.util.Set;

@Getter
@ToString
enum Boss
{
	CRYSTALLINE_HUNLLEF(NpcID.CRYSTALLINE_HUNLLEF, NpcID.CRYSTALLINE_HUNLLEF),
	CRYSTALLINE_HUNLLEF_9022(NpcID.CRYSTALLINE_HUNLLEF_9022, NpcID.CRYSTALLINE_HUNLLEF_9022),
	CRYSTALLINE_HUNLLEF_9023(NpcID.CRYSTALLINE_HUNLLEF_9023, NpcID.CRYSTALLINE_HUNLLEF_9023),
	CRYSTALLINE_HUNLLEF_9024(NpcID.CRYSTALLINE_HUNLLEF_9024, NpcID.CRYSTALLINE_HUNLLEF_9024),
	CORRUPTED_HUNLLEF(NpcID.CORRUPTED_HUNLLEF, NpcID.CORRUPTED_HUNLLEF),
	CORRUPTED_HUNLLEF_9036(NpcID.CORRUPTED_HUNLLEF_9036, NpcID.CORRUPTED_HUNLLEF_9036),
	CORRUPTED_HUNLLEF_9037(NpcID.CORRUPTED_HUNLLEF_9037, NpcID.CORRUPTED_HUNLLEF_9037),
	CORRUPTED_HUNLLEF_9038(NpcID.CORRUPTED_HUNLLEF_9038, NpcID.CORRUPTED_HUNLLEF_9038);

	private final Set<Integer> ids;

	Boss(Integer... ids)
	{
		this.ids = Sets.newHashSet(ids);
	}

	static Boss getBoss(int id)
	{
		for (Boss boss : values())
		{
			if (boss.ids.contains(id))
			{
				return boss;
			}
		}

		return null;
	}

}