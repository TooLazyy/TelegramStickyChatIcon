package com.example.artem.chatscroll;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private LinearLayoutManager manager;
    private static final String TAG = "MainActivity";
    /**
     * threshold value for detecting when to pin
     * user icon to bottom, 10 is enough to do it smoothly
     */
    private static final int THRESHOLD = 10;
    private List<UserMessage> data;

    /**
     * position of the last view where we pinned icon
     * need to detect whether we scrolled to another
     * view or not
     */
    private int lastViewPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMessages = (RecyclerView) findViewById(R.id.rvMessages);
        manager = new LinearLayoutManager(this);
        rvMessages.setLayoutManager(manager);
        data = new ArrayList<>();
        /**
         * populate our adapter with messages
         */
        data.add(new UserMessage(Arrays.asList("message 01\n\n\n\n\n.", "message 11\n\n\n\n\n."), 0));
        data.add(new UserMessage(Arrays.asList("message 02\n\n\n\n\n.", "message 12\n\n\n\n\n."), 1));
        data.add(new UserMessage(Arrays.asList("message 03\n\n\n\n\n.", "message 13\n\n\n\n\n."), 2));
        data.add(new UserMessage(Arrays.asList("message 04\n\n\n\n\n.", "message 14\n\n\n\n\n."), 2));
        data.add(new UserMessage(Arrays.asList("message 05\n\n\n\n\n.", "message 15\n\n\n\n\n."), 0));
        data.add(new UserMessage(Arrays.asList("message 06\n\n\n\n\n.", "message 16\n\n\n\n\n."), 0));
        data.add(new UserMessage(Arrays.asList("message 07\n\n\n\n\n.", "message 17\n\n\n\n\n."), 1));
        data.add(new UserMessage(Arrays.asList("message 08\n\n\n\n\n.", "message 18\n\n\n\n\n."), 1));
        data.add(new UserMessage(Arrays.asList("message 09\n\n\n\n\n.", "message 19\n\n\n\n\n."), 2));
        Collections.reverse(data);
        final MyAdapter adapter = new MyAdapter(data, this);
        rvMessages.setAdapter(adapter);
        rvMessages.scrollToPosition(data.size() - 1);

        /**
         * scroll listener to do the magic
         */
        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //get the last (at bottom) view position
                int viewPosition = manager.findLastVisibleItemPosition();
                //get view by that position
                View itemView = manager.findViewByPosition(viewPosition);
                /**
                 * calculate the distance between our last view (by getting its bottom coordinate)
                 * and our our bottom view which allows to send messages (by getting its top)
                 * that value includes item_message.xml root layout's margins
                 */
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                int difference = Math.abs(getBottomViewTop() -
                        itemView.getBottom() - lp.bottomMargin);
                //not sure that check is needed
                if (itemView != null) {
                    //get the icon to pin
                    View icon = itemView.findViewById(R.id.ivIcon);
                    /**
                     * check for threshold value (when first starting to scroll within view)
                     * or still being inside the same view position
                     */
                    if (difference <= THRESHOLD || viewPosition == lastViewPosition) {
                        //this needed when scrolling up, or difference would has too big value
                        if (difference > (itemView.getHeight() - icon.getHeight())) {
                            difference = itemView.getHeight() - icon.getHeight();
                        }
                        //change icon Y position
                        icon.setTranslationY(-difference);
                    } else if (viewPosition != lastViewPosition) {
                        /**
                         * for too fast scroll we need to get last item view
                         * and manually set its Y translation to zero!
                         */
                        View lastItem = manager.findViewByPosition(lastViewPosition);
                        if (lastItem != null) {
                            View icon2 = lastItem.findViewById(R.id.ivIcon);
                            icon2.setTranslationY(0);
                        }
                        //we scrolled to another view item
                        lastViewPosition = viewPosition;
                    }
                    Log.d(TAG, "----------------------------------------");

                }
            }
        });
    }

    /**
     * we need that method to get Top position of our
     * view, which allows us to send messages (it would be editText with button)
     *
     * @return view top position
     */
    private int getBottomViewTop() {
        return findViewById(R.id.bottom).getTop();
    }

    /**
     * simple recycler adapter for our messages list
     */
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<UserMessage> data;
        private LayoutInflater inflater;

        public MyAdapter(List<UserMessage> data, Context context) {
            this.data = data;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.item_message, parent, false));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            UserMessage item = data.get(position);
            /**
             * clear our view container
             * if you will have only one message,
             * replace LinearLayout by single message view
             */

            holder.llMessages.removeAllViews();
            if (item.userId == 0) {
                holder.icon.setImageResource(R.drawable.icon1);
            } else if (item.userId == 1){
                holder.icon.setImageResource(R.drawable.icon2);
            } else {
                holder.icon.setImageResource(R.drawable.icon3);
            }
            /**
             * populate messages container
             */
            for (String message : item.messages) {
                holder.llMessages.addView(getMessageView(message));
            }
            holder.icon.setTranslationY(0);
        }

        /**
         * inflate and prepare message view
         * you can inflate any view here
         * TextView is here just coz im lazy
         *
         * @param text
         * @return message view
         */
        private View getMessageView(String text) {
            LinearLayout m = (LinearLayout) inflater.inflate(R.layout.item_text, null);
            ((TextView) m.findViewById(R.id.tvText)).setText(text);
            return m;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            LinearLayout llMessages;

            public ViewHolder(View itemView) {
                super(itemView);
                llMessages = (LinearLayout) itemView.findViewById(R.id.llMessages);
                icon = (ImageView) itemView.findViewById(R.id.ivIcon);
            }
        }
    }

    /**
     * class for user messages
     * messages = list of user messages for a short period (few seconds)
     */
    class UserMessage {
        public long userId;
        public List<String> messages = new ArrayList<>();

        public UserMessage(List<String> messages, long userId) {
            this.messages = messages;
            this.userId = userId;
        }
    }
}
