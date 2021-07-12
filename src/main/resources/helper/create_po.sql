create or replace table demand_type
(
	id bigint auto_increment
		primary key,
	active bit not null,
	code varchar(255) null,
	name varchar(255) not null
);

create or replace table garant
(
	id bigint auto_increment
		primary key,
	active bit not null,
	code varchar(255) null,
	name varchar(255) not null
);

create or replace table plan
(
	id bigint auto_increment
		primary key,
	active bit not null,
	code varchar(255) null,
	name varchar(255) not null
);

create or replace table price
(
	id bigint auto_increment
		primary key,
	active bit not null,
	code varchar(255) null,
	name varchar(255) not null
);

create or replace table region
(
	id bigint auto_increment
		primary key,
	active bit not null,
	code varchar(255) null,
	name varchar(255) not null
);

create or replace table safety
(
	id bigint auto_increment
		primary key,
	active bit not null,
	code varchar(255) null,
	name varchar(255) not null
);

create or replace table send
(
	id bigint auto_increment
		primary key,
	active bit not null,
	code varchar(255) null,
	name varchar(255) not null
);

create or replace table status
(
	id bigint auto_increment
		primary key,
	active bit not null,
	code varchar(255) null,
	name varchar(255) not null
);

create or replace table usr
(
	id bigint auto_increment
		primary key,
	activation_code varchar(255) null,
	active bit not null,
	email varchar(255) null,
	password varchar(255) null,
	username varchar(255) null
);

create or replace table demand
(
	id bigint auto_increment
		primary key,
	address varchar(255) null,
	add_act varchar(255) null,
	add_reg varchar(255) null,
	contact varchar(255) null,
	count int null,
	create_date date null,
	demander varchar(255) null,
	done bit not null,
	inn varchar(255) null,
	object varchar(255) null,
	pas_iss varchar(255) null,
	pas_num varchar(255) null,
	pas_ser varchar(255) null,
	reason varchar(255) null,
	dtype_id bigint null,
	garant_id bigint null,
	plan_id bigint null,
	price_id bigint null,
	send_id bigint null,
	status_id bigint null,
	user_id bigint null,
	it_change bit null,
	it_done bit null,
	it_load1c bit null,
	inn_date date null,
	specification varchar(255) null,
	period_connection varchar(255) null,
	contract varchar(255) null,
	constraint FK26873uiifblvp4jldxv66nhv7
		foreign key (status_id) references status (id),
	constraint FKbwog0c1p9kcf0iil6bsdtprd0
		foreign key (garant_id) references garant (id),
	constraint FKcgve6ke4mnd6mkv1t93wvxyxg
		foreign key (send_id) references send (id),
	constraint FKgncduqoocf8ujt64hw9mfl4x7
		foreign key (dtype_id) references demand_type (id),
	constraint FKj6a7myqy9oq0g4psue6or0ukp
		foreign key (plan_id) references plan (id),
	constraint FKjaaxtb3bwp9yqljhtqqph47s
		foreign key (user_id) references usr (id),
	constraint FKr36ry3qeyfdoujd6139cqks7w
		foreign key (price_id) references price (id)
);

create or replace table expiration
(
	id bigint auto_increment
		primary key,
	power_max double null,
	project_date date null,
	step varchar(255) null,
	usage_date date null,
	demand_id bigint null,
	safety_id bigint null,
	constraint FKh35l295skd8qg7psin0nackd7
		foreign key (safety_id) references safety (id),
	constraint FKqiv8cmedhhxj5cethag4faf6v
		foreign key (demand_id) references demand (id)
);

create or replace table file_storage
(
	id bigint auto_increment
		primary key,
	createdate date null,
	link varchar(255) null,
	load1c bit null,
	name varchar(2048) null,
	demand_id bigint null,
	constraint FK1b8ajbgwb2tn4fhggx6fu64js
		foreign key (demand_id) references demand (id)
);

create or replace table general
(
	id bigint auto_increment
		primary key,
	count_generations varchar(255) null,
	count_points int null,
	count_transformations varchar(255) null,
	description varchar(255) null,
	reservation varchar(255) null,
	techmin_generation varchar(255) null,
	demand_id bigint null,
	constraint FKan2bqvd5qbsiwkc1jogjstk8a
		foreign key (demand_id) references demand (id)
);

create or replace table user_role
(
	user_id bigint not null,
	roles varchar(255) null,
	constraint FKfpm8swft53ulq2hl11yplpr5
		foreign key (user_id) references usr (id)
);

create or replace table voltage
(
	id bigint auto_increment
		primary key,
	active bit not null,
	code varchar(255) null,
	name varchar(255) not null
);

create or replace table point
(
	id bigint auto_increment
		primary key,
	pow_max double null,
	pow_cur double null,
	pow_dem double null,
	spec varchar(255) null,
	demand_id bigint null,
	safe_id bigint null,
	volt_id bigint null,
	constraint FK1j5vl5o3mr2ytnogbsooxa7l1
		foreign key (demand_id) references demand (id),
	constraint FKlycdhurmqvvwvc6vgervlb6ee
		foreign key (volt_id) references voltage (id),
	constraint FKsd7xbwox6qmv32qe0mqb0wjtm
		foreign key (safe_id) references safety (id)
);

