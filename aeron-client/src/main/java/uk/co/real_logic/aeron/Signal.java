/*
 * Copyright 2014 Real Logic Ltd.
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
package uk.co.real_logic.aeron;

/**
 * Signal a waiting thread that an event has occurred which it has been waiting on.
 */
class Signal
{
    private boolean signalRaised;

    public Signal()
    {
        signalRaised = false;
    }

    public synchronized void signal()
    {
        if (signalRaised)
        {
            throw new IllegalStateException("Attempting to signal when signal has already been raised");
        }

        signalRaised = true;
        this.notify();
    }

    /**
     * Await a signal with a timeout value in milliseconds.
     *
     * @param awaitTimeout in milliseconds.
     */
    public synchronized void await(final long awaitTimeout)
    {
        try
        {
            final long beginTime = System.currentTimeMillis();
            while (!signalRaised)
            {
                wait(awaitTimeout - (System.currentTimeMillis() - beginTime));
            }
        }
        catch (final InterruptedException ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            signalRaised = false;
        }
    }
}
