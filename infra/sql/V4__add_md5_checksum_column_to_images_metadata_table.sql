ALTER TABLE images_metadata
ADD COLUMN md5_checksum VARCHAR(32);

CREATE INDEX idx_images_metadata_md5_checksum ON images_metadata(md5_checksum);
