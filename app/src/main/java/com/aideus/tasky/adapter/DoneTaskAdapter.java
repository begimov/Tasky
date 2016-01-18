package com.aideus.tasky.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aideus.tasky.R;
import com.aideus.tasky.Utils;
import com.aideus.tasky.fragment.DoneTaskFragment;
import com.aideus.tasky.fragment.TaskFragment;
import com.aideus.tasky.model.Item;
import com.aideus.tasky.model.ModelTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoneTaskAdapter extends TaskAdapter {

    public DoneTaskAdapter(DoneTaskFragment taskFragment) {
        super(taskFragment);
    }

    // Create new views (invoked by the layout manager).
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_task, viewGroup, false);
        TextView title = (TextView) view.findViewById(R.id.tvTaskTitle);
        TextView date = (TextView) view.findViewById(R.id.tvTaskDate);
        CircleImageView priority = (CircleImageView) view.findViewById(R.id.cvTaskPriority);

        return new TaskViewHolder(view, title, date, priority);
    }

    // // Replace the contents of a view (invoked by the layout manager).
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        // Get item (ModelTask) by its position in ArrayList.
        Item item = items.get(position);

        // If item isTask then
        if (item.isTask()) {

            // Set the enabled state of itemView.
            viewHolder.itemView.setEnabled(true);

            // Set task ModelTask from current item.
            final ModelTask task = (ModelTask) item;

            // Set TaskViewHolder from viewHolder.
            final TaskViewHolder taskViewHolder = (TaskViewHolder) viewHolder;

            // Get itemView from taskViewHolder.
            final View itemView = taskViewHolder.itemView;

            // Get resources associated with this itemView.
            final Resources resources = itemView.getResources();

            // Set text of title (TextView) in taskViewHolder with task (ModelTask) title.
            taskViewHolder.title.setText(task.getTitle());

            // If task (ModelTask) has date, set text of date (TextView) in taskViewHolder with task (ModelTask) date.
            if (task.getDate() != 0) {
                taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
            } else {
                taskViewHolder.date.setText(null);
            }

            // Set the enabled state of itemView to VISIBLE - this view is visible.
            itemView.setVisibility(View.VISIBLE);

            // Set the enabled state of this view.
            taskViewHolder.priority.setEnabled(true);

            // Set text color for title and date (TextView).
            taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_disabled_material_light));
            taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_disabled_material_light));

            // Set tinting option for the priority image (CircleImageView) and its image resource.
            taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));
            taskViewHolder.priority.setImageResource(R.drawable.ic_check_circle_white_48dp);

            // Interface definition for a callback to be invoked when a view has been clicked and held.
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // On long click show removeTaskDialog after the specified amount of time elapses.
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getTaskFragment().removeTaskDialog(taskViewHolder.getLayoutPosition());
                        }
                    }, 1000);
                    return true;
                }
            });

            // setOnClickListener to priority (CircleImageView).
            taskViewHolder.priority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Set the disabled state for this view. So only one first click was tracked.
                    taskViewHolder.priority.setEnabled(false);

                    // When CircleImageView clicked, change status of task (ModelTask) to STATUS_CURRENT.
                    task.setStatus(ModelTask.STATUS_CURRENT);

                    // When CircleImageView clicked, update status of task (ModelTask) in database to STATUS_CURRENT.
                    getTaskFragment().activity.dbHelper.update().status(task.getTimeStamp(), ModelTask.STATUS_CURRENT);

                    // Change text color for title and date (TextView) to "CurrentTask" text colors.
                    taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_default_material_light));
                    taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_default_material_light));

                    // Change tinting option for the priority (CircleImageView) to "CurrentTask".
                    taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));

                    // Constructs and returns an ObjectAnimator for priority (CircleImageView) that animates between float values.
                    // Two values imply starting and ending values of rotationY.
                    ObjectAnimator flipIn = ObjectAnimator.ofFloat(taskViewHolder.priority, "rotationY", 180f, 0f);
                    taskViewHolder.priority.setImageResource(R.drawable.ic_checkbox_blank_circle_white_48dp);

                    // Add listener to flipIn (ObjectAnimator) which sends events through the life of an animation, such as start, repeat, and end.
                    flipIn.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        // When rotationY animation ends and if task (ModelTask) status is NOT STATUS_DONE then...
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (task.getStatus() != ModelTask.STATUS_DONE) {

                                // Initialize ObjectAnimator for translationX animation from 0 to -width of itemView (View).
                                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView, "translationX", 0f, -itemView.getWidth());

                                // Initialize ObjectAnimator for translationX animation from -width of itemView (View) to 0.
                                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView, "translationX", -itemView.getWidth(), 0f);

                                // Add listener to translationX (ObjectAnimator) which sends events through the life of an animation.
                                translationX.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                    }

                                    // When translationX (from 0 to -width) animation ends,
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        // Set itemView (View) state to GONE - this view is invisible.
                                        itemView.setVisibility(View.GONE);

                                        // Move task (ModelTask) to current tasks list items (ArrayList) (currentTaskFragment.addTask(task)).
                                        getTaskFragment().moveTask(task);

                                        // Remove task from items (ArrayList) by position of the ViewHolder.
                                        removeItem(taskViewHolder.getLayoutPosition());
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {
                                    }
                                });
                                // Initialize AnimatorSet to play a set of Animator objects in the specified order.
                                // Animations can be set up to play together, in sequence, or after a specified delay.
                                AnimatorSet translationSet = new AnimatorSet();
                                translationSet.play(translationX).before(translationXBack);
                                translationSet.start();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                    // Start animation.
                    flipIn.start();
                }
            });
        }
    }
}
