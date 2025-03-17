CREATE TABLE images_metadata (
    id UUID NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID NOT NULL,
    image_key VARCHAR(255) NOT NULL,
    image_dimension VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_images_metadata_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_images_metadata_user_id ON images_metadata(user_id)
