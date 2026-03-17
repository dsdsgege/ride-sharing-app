                                                                        package hu.ridesharing.entity;

                                                                        import jakarta.persistence.*;
                                                                        import lombok.Data;

                                                                        @Entity
                                                                        @Data
                                                                        @IdClass(RatingId.class)
                                                                        public class Rating {

                                                                            @Id
                                                                            @ManyToOne
                                                                            @JoinColumn(name = "journey_id")
                                                                            private Journey journey;

                                                                            @Id
                                                                            @ManyToOne
                                                                            @JoinColumn(name = "rated_username")
                                                                            private User rated;

                                                                            @Id
                                                                            @ManyToOne
                                                                            @JoinColumn(name = "rater_username")
                                                                            private User rater;

                                                                            @Id
                                                                            @Enumerated(EnumType.STRING)
                                                                            private RatingType type;

                                                                            private double value;

                                                                            private String comment;
                                                                        }