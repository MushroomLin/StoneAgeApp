package com.example.miniresearchdatabase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.models.Comment;
import com.example.miniresearchdatabase.models.Post;
import com.example.miniresearchdatabase.models.User;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends BaseActivity {

    private static final String TAG = "PostDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private DatabaseReference mDatabase;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private String author = "";
    private String currKey = "";


    private CommentAdapter mAdapter;

    private TextView mAuthorView;
    private TextView mTitleView;
    private TextView mBodyView;
    private TextView mAddressView;
    private EditText mCommentField;
    private Button mCommentButton;
    private Button mOfferButton;
    private ImageView button_back;
    private RecyclerView mCommentsRecycler;
    private TextView textView_estimatedPrice;
    private ImageView mPictureView;
    private ImageView mAuthorPhoto;
    private TextView starTextView;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(mPostKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments").child(mPostKey);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Initialize Views
        mAuthorView = findViewById(R.id.postAuthor);
        mTitleView = findViewById(R.id.postTitle);
        mBodyView = findViewById(R.id.postBody);
        mAddressView = findViewById(R.id.postAddress);
        mCommentField = findViewById(R.id.fieldCommentText);
        mCommentButton = findViewById(R.id.buttonPostComment);
        mOfferButton = findViewById(R.id.button_offer);
        mPictureView = findViewById(R.id.pictureImageView);
        mAuthorPhoto = findViewById(R.id.postAuthorPhoto);
        button_back = findViewById(R.id.BackImageView);
        starTextView = findViewById(R.id.starTextView);
        mCommentsRecycler = findViewById(R.id.recyclerPostComments);
        textView_estimatedPrice = findViewById(R.id.textView_estimatedPrice);

        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostDetailActivity.this, MainActivity.class));
            }
        });

        mOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, SendOfferActivity.class);
                intent.putExtra(SendOfferActivity.OFFER_POST_KEY, mPostKey);
                startActivity(intent);
            }
        });

        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onStart() {
        super.onStart();
        // Add value event listener to the post
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                if (post!=null && post.uid!=null) {
                    DatabaseReference ref = mDatabase.child("users").child(post.uid);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Serialize retrieved data to a User object
                            User user = dataSnapshot.getValue(User.class);
                            //Now you have an object of the User class and can use its getters like this
                            if (user != null) {
                                // Set the user profile picture
                                if (user.avatar != null) {
                                    mAuthorPhoto.setImageBitmap(user.getAvatar());
                                }
                                starTextView.setText( String.format("%.1f", user.rate)+"/5.0");
                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                        }
                    });
                }
                else{
                    mAuthorPhoto.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_baseline_person_24px));
                }
                mAuthorView.setText(post.author);
                author = post.author;
                currKey =post.uid;
                mAuthorPhoto.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        // Launch ChatActivity
                        Intent intent = new Intent(PostDetailActivity.this, ChatActivity.class);
                        intent.putExtra("username", author);
                        intent.putExtra("userId", currKey);
                        startActivity(intent);
                    }
                });
                mTitleView.setText(post.title);
                mBodyView.setText(post.description);
                mAddressView.setText(post.address);
                if(post.picture!=null && (!post.picture.equals(""))){
                    mPictureView.setImageBitmap(ImageUtils.stringToBitmap(post.picture));
                    mPictureView.setVisibility(View.VISIBLE);
                }
                else{
                    mPictureView.setVisibility(View.GONE);
                }
                if(post.estimatedPrices == null) {
                    textView_estimatedPrice.setText("No Estimated Price Now");
                } else {
                    double sumPrice = 0.0;
                    for(double price : post.estimatedPrices) {
                        sumPrice += price;
                    }


                    int priceCount = post.estimatedPrices.size();
                    int avgPrice = (int)(sumPrice / priceCount);

                    textView_estimatedPrice.setText("Estimated: $" + avgPrice);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(PostDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();

            }
        };

        mPostReference.addValueEventListener(postListener);


        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;

        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }


    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.username;

                        // Create new comment object
                        String commentText = mCommentField.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        // Push the comment, it will appear in the list
                        mCommentsReference.push().setValue(comment);

                        // Clear the field
                        mCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;
        public ImageView authorPicture;

        public CommentViewHolder(View itemView) {
            super(itemView);

            authorView = itemView.findViewById(R.id.commentAuthor);
            bodyView = itemView.findViewById(R.id.commentBody);
            authorPicture = itemView.findViewById(R.id.commentPhoto);
        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private DatabaseReference mDatabase;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            // Create child event listener
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    // Update RecyclerView
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mComments.set(commentIndex, newComment);

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);

            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.text);
            if (comment.uid!=null) {
                DatabaseReference ref = mDatabase.child("users").child(comment.uid);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Serialize retrieved data to a User object
                        User user = dataSnapshot.getValue(User.class);
                        //Now you have an object of the User class and can use its getters like this
                        if (user != null) {
                            // Set the user profile picture
                            if (user.avatar != null) {
                                holder.authorPicture.setImageBitmap(user.getAvatar());
                            }

                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
            else{
                holder.authorPicture.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_person_24px));
            }
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }
}
