/*
 * Copyright (C) 2006 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codewise.util.memory;

import com.google.common.annotations.GwtCompatible;
import junit.framework.TestCase;

/**
 * Unit test for {@link Preconditions}.
 *
 * @author Kevin Bourrillion
 * @author Jared Levy
 */
@GwtCompatible(emulated = true)
public class PreconditionsTest extends TestCase {
    public void testCheckArgument_simple_success() {
        Preconditions.checkArgument(true);
    }

    public void testCheckArgument_simple_failure() {
        try {
            Preconditions.checkArgument(false);
            fail("no exception thrown");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testCheckState_simple_success() {
        Preconditions.checkState(true);
    }

    public void testCheckState_simple_failure() {
        try {
            Preconditions.checkState(false);
            fail("no exception thrown");
        } catch (IllegalStateException expected) {
        }
    }
}
