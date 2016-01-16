package vdsl.queue.delete;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class EmptyQ
{
   public int port;
   public String hostname;
   public String channel;
   public String qManager;
   public String inputQName;

   public EmptyQ()
   {
      super();
   }

   public void init(String queueManager , String hostName , int portNum, String outputQueue)
 {
		hostname = hostName;
		channel = "SYSTEM.ADMIN.SVRCONN";
		qManager = queueManager;
		inputQName = outputQueue;
		port = portNum;
		// Set up MQ environment
		MQEnvironment.hostname = hostname;
		MQEnvironment.channel = channel;
		MQEnvironment.port = port;
		MQException.log = null; 
 }
   /**
    * Connect to a queue manager, open a queue then destructively get (delete)
    * all messages on the queue.   
    */
   public void emptyIt()
   {
      boolean loopAgain = true;
      MQQueueManager _queueManager = null;
      MQQueue queue = null;
      int openOptions = MQC.MQOO_INQUIRE + MQC.MQOO_FAIL_IF_QUIESCING + MQC.MQOO_INPUT_SHARED;
//      System.out.println("EmptyQ Open Options");

      try
      {
         _queueManager = new MQQueueManager(qManager);
//         System.out.println("EmptyQ: Connected to queue manager "+qManager);
         
         try
         {
            queue = _queueManager.accessQueue(inputQName, openOptions, null, null, null);
//            System.out.println("EmptyQ: Opened queue "+inputQName);
            
            MQGetMessageOptions getOptions = new MQGetMessageOptions();
            getOptions.options = MQC.MQGMO_NO_WAIT + MQC.MQGMO_FAIL_IF_QUIESCING + MQC.MQGMO_ACCEPT_TRUNCATED_MSG;
            
            MQMessage message;
            while (loopAgain)
            {
               message = new MQMessage();
               try
               {
                  queue.get(message, getOptions, 1);
               }
               catch (MQException e)
               {
                  if (e.completionCode == 1 && e.reasonCode == MQException.MQRC_TRUNCATED_MSG_ACCEPTED)
                  {
                      // Just what we expected!!
                  }
                  else
                  {
                     loopAgain = false;
                     if (e.completionCode == 2 && e.reasonCode == MQException.MQRC_NO_MSG_AVAILABLE)
                     {
                        // Good, we are now done - no error!!
                     }
                     else
                     {
                        System.err.println("EmptyQ: MQException: " + e.getLocalizedMessage());
                     }
                  }
               }
            }
//            System.out.println(inputQName + " Queue emptied.");
         }
         catch (MQException e1)
         {
            System.err.println("EmptyQ: MQException: " + e1.getLocalizedMessage());
         }
         finally
         {
//            if (queue != null)
//            {
//               queue.close();
//               System.out.println("EmptyQ: Closed queue "+inputQName);
//            }

            if (_queueManager != null)
            {
               _queueManager.disconnect();
//               System.out.println("EmptyQ: Disconnect from "+qManager);
            }
         }
      }
      catch (MQException e1)
      {
         System.err.println("EmptyQ: MQException: " + e1.getLocalizedMessage());
      }
   }
}
