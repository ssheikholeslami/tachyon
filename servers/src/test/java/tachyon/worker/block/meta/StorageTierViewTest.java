/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.worker.block.meta;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Sets;

import tachyon.worker.block.BlockMetadataManager;
import tachyon.worker.block.BlockMetadataManagerView;
import tachyon.worker.block.evictor.EvictorTestUtils;

public class StorageTierViewTest {
  private static final int TEST_TIER_LEVEL = 0;
  private StorageTier mTestTier;
  private StorageTierView mTestTierView;

  @Rule
  public TemporaryFolder mTestFolder = new TemporaryFolder();

  @Rule
  public ExpectedException mThrown = ExpectedException.none();

  @Before
  public void before() throws Exception {
    File tempFolder = mTestFolder.newFolder();
    BlockMetadataManager metaManager =
        EvictorTestUtils.defaultMetadataManager(tempFolder.getAbsolutePath());
    BlockMetadataManagerView metaManagerView =
        new BlockMetadataManagerView(metaManager, Sets.<Integer>newHashSet(),
            Sets.<Long>newHashSet());
    mTestTier = metaManager.getTiers().get(TEST_TIER_LEVEL);
    mTestTierView = new StorageTierView(mTestTier, metaManagerView);
  }

  @Test
  public void getDirViewsTest() {
    Assert.assertEquals(EvictorTestUtils.TIER_PATH[TEST_TIER_LEVEL].length, mTestTierView
        .getDirViews().size());
  }

  @Test
  public void getDirViewTest() throws Exception {
    for (int index = 0; index < EvictorTestUtils.TIER_PATH[TEST_TIER_LEVEL].length; index ++) {
      Assert.assertEquals(index, mTestTierView.getDirView(index).getDirViewIndex());
    }
  }

  @Test
  public void getDirViewBadIndexTest() throws Exception {
    mThrown.expect(IndexOutOfBoundsException.class);
    int badDirIndex = EvictorTestUtils.TIER_PATH[TEST_TIER_LEVEL].length;
    Assert.assertEquals(badDirIndex, mTestTierView.getDirView(badDirIndex).getDirViewIndex());
  }

  @Test
  public void getTierViewAliasTest() {
    Assert.assertEquals(mTestTier.getTierAlias(), mTestTierView.getTierViewAlias());
  }

  @Test
  public void getTierViewLevelTest() {
    Assert.assertEquals(mTestTier.getTierLevel(), mTestTierView.getTierViewLevel());
  }
}
