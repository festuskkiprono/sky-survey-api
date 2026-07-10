create table surveys(
                        id serial primary key,
                        name varchar(150) not null,
                        description text,
                        status varchar(20) not null default 'DRAFT',
                        created_at timestamp not null default current_timestamp,
                        updated_at timestamp not null default current_timestamp,
                        deleted_at timestamp,

                        constraint survey_status_check check (status in ('DRAFT','ACTIVE','INACTIVE'))
);

create table questions(
                          id serial primary key,
                          survey_id int not null,
                          name varchar(100) not null,
                          question_text text not null,
                          description text,
                          type varchar(20) not null,
                          status varchar(20) not null default 'ACTIVE',
                          required boolean not null default false,
                          display_order int not null,
                          created_at timestamp not null default current_timestamp,
                          updated_at timestamp not null default current_timestamp,
                          deleted_at timestamp,
                          max_length int,
                          allow_multiple boolean,
                          min_selection int,
                          max_selection int,
                          file_format varchar(50),
                          max_file_size int,
                          max_file_size_unit varchar(10),
                          allow_multiple_files boolean,

                          constraint fk_question_survey foreign key (survey_id) references surveys(id),
                          constraint unique_question_name_per_survey unique (survey_id, name),
                          constraint question_type_check check (type in ('short_text','long_text','email','choice','file')),
                          constraint question_status_check check (status in ('ACTIVE','INACTIVE')),
                          constraint file_size_unit_check check (max_file_size_unit is null or max_file_size_unit in ('kb','mb'))
);

create index idx_questions_survey on questions(survey_id);

create table options(
                        id serial primary key,
                        question_id int not null,
                        option_value varchar(100) not null,
                        label varchar(150) not null,
                        display_order int not null,
                        deleted_at timestamp,

                        constraint fk_option_question foreign key (question_id) references questions(id),
                        constraint unique_option_value_per_question unique (question_id, option_value)
);

create index idx_options_question on options(question_id);

create table responses(
                          id serial primary key,
                          survey_id int not null,
                          email_address varchar(150) not null,
                          date_responded timestamp not null default current_timestamp,

                          constraint fk_response_survey foreign key (survey_id) references surveys(id)
);

create index idx_responses_survey_email on responses(survey_id, email_address);

create table answers(
                        id serial primary key,
                        response_id int not null,
                        question_id int not null,
                        value_text text,

                        constraint fk_answer_response foreign key (response_id) references responses(id),
                        constraint fk_answer_question foreign key (question_id) references questions(id),
                        constraint unique_answer_per_question_per_response unique (response_id, question_id)
);

create index idx_answers_response on answers(response_id);
create index idx_answers_question on answers(question_id);

create table answer_options(
                               answer_id int not null,
                               option_id int not null,

                               constraint pk_answer_options primary key (answer_id, option_id),
                               constraint fk_ao_answer foreign key (answer_id) references answers(id),
                               constraint fk_ao_option foreign key (option_id) references options(id)
);

create index idx_answer_options_option on answer_options(option_id);

create table uploaded_files(
                               id serial primary key,
                               answer_id int not null,
                               original_filename varchar(255) not null,
                               storage_path varchar(255) not null,
                               file_size int not null,
                               file_type varchar(100) not null,
                               uploaded_at timestamp not null default current_timestamp,

                               constraint fk_file_answer foreign key (answer_id) references answers(id),
                               constraint chk_file_size check (file_size >= 0 and file_size <= 52428800)
);

create index idx_uploaded_files_answer on uploaded_files(answer_id);