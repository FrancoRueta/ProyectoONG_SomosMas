package com.restteam.ong.services.impl;

import com.restteam.ong.models.Slide;
import com.restteam.ong.services.SlideService;

public class SlideServiceImpl implements SlideService {


    @Override
    public void addSlide(Slide slide) {
        //TODO: pasar imagen de string a Byte[].
        if(slide.getNumberOrder() == null){
            try{
                slide.setNumberOrder(lastSlideOfDB().getNumberOrder());
            }catch(Exception ex){
                slide.setNumberOrder(0);
            }
        }
        slideRepository.save(slide);
    }

    //Si lo van a usar tener cuidado, tira error.
    private Slide lastSlideOfDB(){ return slideRepository.findTopByOrderByIdDesc().orElseThrow(
            () -> new IllegalStateException("There's no slides on database.")); }

    @Override
    public void deleteSlide(Long slideID){
        boolean exists = slideRepository.existsById(slideID);
        if(!exists){
            throw new IllegalStateException("");
        }
        slideRepository.deleteById(slideID);
    }


    @Override
    public Slide getSlideById(Long id) {
        return slideRepository.findById(id).orElseThrow(
                () -> new IllegalStateException(String.format(SLIDE_NOT_FOUND,id))
        );
    }
    
    @Override
    public ArrayList<Slide> getAllSlides() {
        return (ArrayList<Slide>) slideRepository.findAll();
    }

}
